import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.firdan.storyapp.adapter.StoriesAdapter
import com.firdan.storyapp.data.local.entity.UserModel
import com.firdan.storyapp.data.local.pref.StoryPref
import com.firdan.storyapp.data.repository.StoryRepository
import com.firdan.storyapp.data.repository.UserRepository
import com.firdan.storyapp.ui.viewmodels.DummyData
import com.firdan.storyapp.ui.viewmodels.MainDispatcherRule
import com.firdan.storyapp.ui.viewmodels.MainViewModel
import com.firdan.storyapp.ui.viewmodels.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var loginPreferences: StoryPref

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(storyRepository, userRepository, loginPreferences)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `getStories should not be null`() = runTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val dummyStories = DummyData.generateDummyStory()
        val data: PagingData<UserModel> = PagingData.from(dummyStories)
        val stories = MutableLiveData<PagingData<UserModel>>().apply { value = data }

        Mockito.`when`(storyRepository.getStories(dummyToken)).thenReturn(stories)
        val actualStories = viewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRules.dispatcher,
            workerDispatcher = mainDispatcherRules.dispatcher
        )

        differ.submitData(actualStories)
        advanceUntilIdle()

        Mockito.verify(storyRepository).getStories(dummyToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `getStories should return no data when stories are empty`() = runTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val dummyEmptyStories = emptyList<UserModel>()
        val data: PagingData<UserModel> = PagingData.from(dummyEmptyStories)
        val stories = MutableLiveData<PagingData<UserModel>>().apply { value = data }

        Mockito.`when`(storyRepository.getStories(dummyToken)).thenReturn(stories)
        val actualStories = viewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRules.dispatcher,
            workerDispatcher = mainDispatcherRules.dispatcher
        )

        differ.submitData(actualStories)
        advanceUntilIdle()

        Mockito.verify(storyRepository).getStories(dummyToken)
        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
