package com.self.lovenotes.data.remote.repository

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.util.utils.getMonthRange
import io.mockk.Ordering
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import java.time.YearMonth

@ExtendWith(MockKExtension::class)
class DateMemoryRepositoryTest {
    lateinit var firestore: FirebaseFirestore
    lateinit var collectionReference: CollectionReference
    lateinit var documentReference: DocumentReference

    lateinit var dateMemoryRepository: DateMemoryRepository

    @BeforeEach
    fun setUp() {
        firestore = mockk<FirebaseFirestore>()
        collectionReference = mockk<CollectionReference>(relaxed = true)
        documentReference = mockk<DocumentReference>(relaxed = true)
        dateMemoryRepository = DateMemoryRepository(firestore)
    }

    @Test
    fun `ID가 있는 Memory입력시 기존 Memory를 업데이트`() = runTest {
        val testMemory = DateMemory(id = "test_id", geoList = emptyList())

        every { firestore.collection("date_memories") } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.set(testMemory.toMap()) } returns Tasks.forResult(null)

        dateMemoryRepository.updateDateMemory(testMemory)

        verify { documentReference.set(testMemory.toMap()) }
    }

//    @Test
//    fun `ID가 없는 Memory 입력시 기존 Memory를 업데이트`() = runTest {
//        // Given
//        val testMemory = DateMemory(geoList = emptyList()) // ID 비어있음 → add 분기
//        val toMap = testMemory.toMap()
//
//        // Mock 객체 생성
//        val documentReference = mockk<DocumentReference>()
//        val task = Tasks.forResult(documentReference)
//        val slot = slot<OnSuccessListener<DocumentReference>>()
//
//        every { firestore.collection("date_memories") } returns collectionReference
//        every { collectionReference.add(toMap) } returns task
//        every { documentReference.update("id", any()) } returns Tasks.forResult(null)
//        every { documentReference.update("timeStamp", any()) } returns Tasks.forResult(null)
//        every { task.addOnSuccessListener(capture(slot)) } returns Tasks.forResult(null)
//
//        // When
//        dateMemoryRepository.updateDateMemory(testMemory)
//        slot.captured.onSuccess(documentReference)
//
//        // Then
//        verify(ordering = Ordering.SEQUENCE) {
//            firestore.collection("date_memories")
//            collectionReference.add(toMap)
//            documentReference.update("id", any())
//            documentReference.update("timeStamp", any())
//        }
//    }

}