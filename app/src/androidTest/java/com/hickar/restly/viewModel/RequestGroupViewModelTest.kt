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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestGroupViewModelTest {
    private val repository = mockk<CollectionRepository>()
    private val viewModel = RequestGroupViewModel(SavedStateHandle(), repository)

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun afterAll() {
        clearMocks(repository)
    }

    @Test
    fun testLoadExistingGroup() = runTest {
        val expectedGroup = RequestDirectory(
            id = "test-group-uuid",
            name = "test group",
            description = null
        )

        coEvery { repository.getRequestGroupById(expectedGroup.id) } returns flowOf<RequestDirectory?>(expectedGroup)

        viewModel.loadRequestGroup(expectedGroup.id)

        assertEquals(expectedGroup.id, viewModel.group.value.id)
        assertEquals(expectedGroup.name, viewModel.group.value.name)
        assertEquals(expectedGroup.description, viewModel.group.value.description)
        assertEquals(expectedGroup.requests, viewModel.group.value.requests)
        assertEquals(expectedGroup.subgroups, viewModel.group.value.subgroups)

        coVerify(exactly = 1) { repository.getRequestGroupById(expectedGroup.id) }
    }

    @Test
    fun testLoadNonExistingGroup() = runTest {
        val expectedDefaultGroup = RequestDirectory.getDefault()

        coEvery { repository.getRequestGroupById(RequestDirectory.DEFAULT_ID) } returns flowOf<RequestDirectory?>(null)
        coEvery { repository.insertRequestGroup(RequestDirectory.getDefault()) } just Runs

        viewModel.loadRequestGroup(null)

        assertEquals(viewModel.group.value, expectedDefaultGroup)

        coVerify(exactly = 1) { repository.insertRequestGroup(RequestDirectory.getDefault()) }
        coVerify(exactly = 1) { repository.getRequestGroupById(RequestDirectory.DEFAULT_ID) }
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

        coVerify(exactly = 1) { repository.deleteRequestItem(initialGroup.requests[deleteAtPosition]) }
        coVerify(exactly = 1) { repository.getRequestGroupById(initialGroup.id) }
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

        coVerify(exactly = 1) { repository.getRequestGroupById(initialGroup.id) }
        coVerify(exactly = 1) { repository.deleteRequestGroup(initialGroup.subgroups[deleteAtPosition]) }
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

        coVerify(exactly = 1) { repository.getRequestGroupById(initialGroup.id) }
        coVerify(exactly = 1) { repository.insertRequestItem(newRequestItem.captured) }
    }
}