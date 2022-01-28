package com.hickar.restly.viewModel

import androidx.lifecycle.SavedStateHandle
import com.hickar.restly.models.RequestDirectory
import com.hickar.restly.models.RequestItem
import com.hickar.restly.repository.room.CollectionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestGroupViewModelTest {
    private val repository = mockk<CollectionRepository>()
    private val viewModel = RequestGroupViewModel(SavedStateHandle(), repository)

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterAll
    fun afterAll() {
        clearMocks(repository)
    }

    @Test
    fun testLoadExistingGroup() = runTest {
        val id = "test-group-uuid"
        val group = RequestDirectory(
            id = "test-group-uuid",
            name = "test group",
            description = null
        )

        coEvery { repository.getRequestGroupById(id) } returns flowOf<RequestDirectory?>(group)

        viewModel.loadRequestGroup(id)

        assertEquals(group.id, viewModel.group.value.id)
        assertEquals(group.name, viewModel.group.value.name)
        assertEquals(group.description, viewModel.group.value.description)
        assertEquals(group.requests, viewModel.group.value.requests)
        assertEquals(group.subgroups, viewModel.group.value.subgroups)

        coVerifyAll {
            repository.getRequestGroupById(id)
        }
    }

    @Test
    fun testLoadNonExistingGroup() = runTest {
        val defaultGroup = RequestDirectory.getDefault()

        coEvery { repository.getRequestGroupById(RequestDirectory.DEFAULT_ID) } returns flowOf<RequestDirectory?>(null)
        coEvery { repository.insertRequestGroup(RequestDirectory.getDefault()) } just Runs

        viewModel.loadRequestGroup(null)

        assertEquals(viewModel.group.value.id, defaultGroup.id)
        assertEquals(viewModel.group.value.name, defaultGroup.name)
        assertEquals(viewModel.group.value.description, defaultGroup.description)
        assertEquals(viewModel.group.value.requests, defaultGroup.requests)
        assertEquals(viewModel.group.value.subgroups, defaultGroup.subgroups)

        coVerifyAll {
            repository.getRequestGroupById(RequestDirectory.DEFAULT_ID)
            repository.insertRequestGroup(RequestDirectory.getDefault())
        }
    }

    @Test
    fun testDeleteRequest() = runTest {
        val deleteAtPosition = 1

        val initialGroup = RequestDirectory(
            requests = mutableListOf(
                RequestItem(name = "shouldPersist", parentId = "1"),
                RequestItem(name = "shouldBeDeleted", parentId = "1")
            ),
            id = "1"
        )

        val changedGroup = RequestDirectory(
            requests = mutableListOf(
                RequestItem(name = "shouldPersist", parentId = "1"),
            ),
            id = "1"
        )

        val groupChannel = Channel<RequestDirectory?>()

        coEvery { repository.getRequestGroupById(initialGroup.id) } returns groupChannel.consumeAsFlow()
        coEvery { repository.deleteRequestItem(initialGroup.requests[deleteAtPosition]) } just Runs

        viewModel.loadRequestGroup(initialGroup.id)
        groupChannel.send(initialGroup)
        assertEquals(viewModel.group.value, initialGroup)

        viewModel.deleteRequest(deleteAtPosition)
        groupChannel.send(changedGroup)
        assertEquals(viewModel.group.value, changedGroup)

        coVerifyAll {
            repository.getRequestGroupById(initialGroup.id)
            repository.deleteRequestItem(initialGroup.requests[deleteAtPosition])
        }
    }

    @Test
    fun testDeleteSubgroup() = runTest {
        val deleteAtPosition = 1

        val initialGroup = RequestDirectory(
            id = "1",
            subgroups = mutableListOf(
                RequestDirectory(name = "shouldPersist", parentId = "1"),
                RequestDirectory(name = "shouldBeDeleted", parentId = "1")
            )
        )

        val changedGroup = RequestDirectory(
            id = "1",
            subgroups = mutableListOf(
                RequestDirectory(parentId = "1"),
            )
        )

        val groupChannel = Channel<RequestDirectory?>()

        coEvery { repository.getRequestGroupById("1") } returns groupChannel.consumeAsFlow()
        coEvery { repository.deleteRequestGroup(initialGroup.subgroups[deleteAtPosition]) } just Runs

        viewModel.loadRequestGroup(initialGroup.id)
        groupChannel.send(initialGroup)

        viewModel.deleteFolder(deleteAtPosition)
        groupChannel.send(changedGroup)

        assertEquals(viewModel.group.value, changedGroup)

        coVerifyAll {
            repository.getRequestGroupById(initialGroup.id)
            repository.deleteRequestGroup(initialGroup.subgroups[deleteAtPosition])
        }
    }

    @Test
    fun testCreateNewDefaultRequest() = runTest {
        val initialGroup = RequestDirectory(id = "1")
        val newRequestItem = slot<RequestItem>()
        val groupChannel = Channel<RequestDirectory?>()

        coEvery { repository.getRequestGroupById(initialGroup.id) } returns groupChannel.consumeAsFlow()
        coEvery { repository.insertRequestItem(request = capture(newRequestItem)) } just Runs

        viewModel.loadRequestGroup(initialGroup.id)
        groupChannel.send(initialGroup)

        viewModel.createNewDefaultRequest()

        val changedGroup = RequestDirectory(
            id = "1",
            requests = mutableListOf(newRequestItem.captured)
        )

        groupChannel.send(changedGroup)

        assertNotEquals(initialGroup, changedGroup)
        assertEquals(viewModel.group.value.requests[0].id, newRequestItem.captured.id)
        assertEquals(viewModel.group.value.requests[0].parentId, initialGroup.id)

        coVerifyAll {
            repository.getRequestGroupById(initialGroup.id)
            repository.insertRequestItem(newRequestItem.captured)
        }
    }
}