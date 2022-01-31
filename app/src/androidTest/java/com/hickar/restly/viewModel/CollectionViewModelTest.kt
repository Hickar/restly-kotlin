package com.hickar.restly.viewModel

import androidx.lifecycle.SavedStateHandle
import com.hickar.restly.models.Collection
import com.hickar.restly.models.CollectionOrigin
import com.hickar.restly.repository.room.CollectionRepository
import com.hickar.restly.testUtils.random
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectionViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = mockk<CollectionRepository>()
    private val viewModel = CollectionViewModel(SavedStateHandle(), repository)

    @BeforeEach
    private fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    private fun tearDown() {
        clearMocks(repository)
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadExistingCollection() = runTest {
        val expectedCollection = generateCollection()

        coEvery { repository.getCollectionById(expectedCollection.id) } returns flowOf<Collection?>(expectedCollection)

        viewModel.loadCollection(expectedCollection.id)

        assertEquals(expectedCollection.id, viewModel.collection.value.id)
        assertEquals(expectedCollection.name, viewModel.collection.value.name)
        assertEquals(expectedCollection.description, viewModel.collection.value.description)
        assertEquals(expectedCollection.owner, viewModel.collection.value.owner)
        assertEquals(expectedCollection.parentId, viewModel.collection.value.parentId)
        assertEquals(expectedCollection.origin, viewModel.collection.value.origin)

        coVerify(exactly = 1) { repository.getCollectionById(expectedCollection.id) }
    }

    @Test
    fun testLoadNonExistingCollection() = runTest {
        viewModel.loadCollection(Collection.DEFAULT_ID)
        viewModel.loadCollection(null)

        verifyAll { repository wasNot Called }
    }

    @Test
    fun testSetName() = runTest {
        val newName = "collection-new-name"
        val initialCollection = generateCollection()
        val expectedCollection = initialCollection.copy(name = newName)

        coEvery { repository.getCollectionById(initialCollection.id) } returns flowOf<Collection?>(initialCollection)

        viewModel.loadCollection(initialCollection.id)
        viewModel.setName(newName)

        assertEquals(viewModel.collection.value, expectedCollection)

        coVerify(exactly = 1) { repository.getCollectionById(initialCollection.id) }
    }

    @Test
    fun testSetDescription() = runTest {
        val newDescription = "new-description"
        val initialCollection = generateCollection()
        val expectedCollection = initialCollection.copy(description = newDescription)

        coEvery { repository.getCollectionById(initialCollection.id) } returns flowOf<Collection?>(initialCollection)

        viewModel.loadCollection(initialCollection.id)
        viewModel.setDescription(newDescription)

        assertEquals(viewModel.collection.value, expectedCollection)

        coVerify(exactly = 1) { repository.getCollectionById(initialCollection.id) }
    }

    @Test
    fun testSaveCollection() = runTest {
        val newName = "new-name"
        val newDescription = "new-description"

        val initialCollection = generateCollection()
        val expectedCollection = initialCollection.copy(name = newName, description = newDescription)

        val collectionChannel = Channel<Collection>()

        coEvery { repository.getCollectionById(initialCollection.id) } returns collectionChannel.consumeAsFlow()
        coEvery { repository.updateCollection(expectedCollection) } just Runs

        viewModel.loadCollection(initialCollection.id)
        collectionChannel.send(initialCollection)
        viewModel.setName(newName)
        viewModel.setDescription(newDescription)
        viewModel.saveCollection()

        collectionChannel.send(expectedCollection)

        assertEquals(viewModel.collection.value, expectedCollection)

        coVerify(exactly = 1) { repository.getCollectionById(initialCollection.id) }
        coVerify(exactly = 1) { repository.updateCollection(expectedCollection) }
    }

    private fun generateCollection(
        id: String = "",
        name: String = "",
        description: String = "",
        owner: String = "",
        parentId: String? = null,
        origin: CollectionOrigin = CollectionOrigin.LOCAL
    ): Collection {
        return Collection(
            id = if (id.isEmpty()) UUID.randomUUID().toString() else id,
            name = if (name.isEmpty()) "".random() else name,
            description = if (description.isEmpty()) "".random() else description,
            owner = if (owner.isEmpty()) "".random() else owner,
            parentId = parentId ?: "".random(),
            origin = origin
        )
    }


}
