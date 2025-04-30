package com.self.lovenotes.data.remote.repository

import app.cash.turbine.test
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.self.lovenotes.data.remote.model.Event
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.YearMonth

@ExtendWith(MockKExtension::class)
class EventRepositoryTest {
    @MockK
    lateinit var firestore: FirebaseFirestore

    private lateinit var repository: EventRepository

    @RelaxedMockK
    lateinit var collectionReference: CollectionReference

    @RelaxedMockK
    lateinit var documentReference: DocumentReference

    @RelaxedMockK
    lateinit var query: Query

    @RelaxedMockK
    lateinit var mockDocumentSnapshot: DocumentSnapshot

    // MockK에서는 ArgumentCaptor 대신 slot 사용
    private val snapshotListenerSlot = slot<EventListener<QuerySnapshot>>()

    @BeforeEach
    fun setUp() {
        repository = EventRepository(firestore)

        // FirebaseFirestore 기본 모킹 설정
        every { firestore.collection("events") } returns collectionReference
    }

    @Test
    fun `updateEvent는 Firestore에 이벤트를 저장한다`() = runTest {
        // Given
        val event = Event(title = "test_id", date = "2025-04-29")

        every { collectionReference.document(event.id) } returns documentReference
        every { documentReference.set(event) } returns Tasks.forResult(null)

        // When
        repository.updateEvent(event)

        // Then
        verify { documentReference.set(event) }
    }

    @Test
    fun `deleteEvent는 Firestore의 이벤트를 제거한다`() = runTest {
        // Arrange
        val event = Event(title = "test_id", date = "2025-04-29")

        every { collectionReference.document(event.id) } returns documentReference
        every { documentReference.delete() } returns Tasks.forResult(null)

        // Act
        repository.deleteEvent(event)

        // Assert
        verify { documentReference.delete() }
    }

    @Test
    fun `getEventsMontlyFlow는 스냅샷이 갱신되면 올바른 이벤트 리스트를 발행해야 한다`() = runTest {
        val uids = listOf("testUid1", "testUid2")
        val yearMonth = YearMonth.of(2025,4)

        // MockK에서는 any()와 같은 매처를 사용할 때 타입 지정이 필요함
        every { collectionReference.whereIn("uid", any<List<String>>()) } returns query
        every { query.whereGreaterThanOrEqualTo("date", any()) } returns query
        every { query.whereLessThanOrEqualTo("date", any()) } returns query

        val mockListenerRegistration = mockk<ListenerRegistration>()
        every { query.addSnapshotListener(capture(snapshotListenerSlot)) } returns mockListenerRegistration

        // When
        val flow = repository.getEventsMontlyFlow(uids, yearMonth)

        flow.test {
            // Fake 문서 Mock
            val mockDocumentSnapshot2 = mockk<QueryDocumentSnapshot>(relaxed = true)
            every { mockDocumentSnapshot2.getString("title") } returns "test"
            every { mockDocumentSnapshot2.getString("date") } returns "2025-04-29"
            every { mockDocumentSnapshot2.getString("uid") } returns "uid1"
            every { mockDocumentSnapshot2.getString("id") } returns "event1"


            // Snapshot Mock
            val mockQuerySnapshot = mockk<QuerySnapshot>()
            every { mockQuerySnapshot.iterator() } returns mutableListOf(mockDocumentSnapshot2).iterator()

            // 리스너 수동 호출 (콜백 트리거)
            snapshotListenerSlot.captured.onEvent(mockQuerySnapshot, null)

            // Then: Flow 발행 값 검증
            val events = awaitItem()
            assertEquals(1, events.size)
            assertEquals("event1", events[0].id)
            assertEquals("test", events[0].title)
            assertEquals("2025-04-29", events[0].date)
            assertEquals("uid1", events[0].uid)

            cancelAndIgnoreRemainingEvents()
        }
    }
}