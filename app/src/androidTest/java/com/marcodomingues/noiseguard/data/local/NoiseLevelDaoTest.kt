package com.marcodomingues.noiseguard.data.local

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.marcodomingues.noiseguard.utils.BASE_TIMESTAMP
import com.marcodomingues.noiseguard.utils.createInMemoryDatabase
import com.marcodomingues.noiseguard.utils.createTestReading
import com.marcodomingues.noiseguard.utils.hoursAgo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class NoiseLevelDaoTest {

    private lateinit var db: NoiseGuardDatabase
    private lateinit var dao: NoiseLevelDao

    @Before
    fun setup() {
        db = createInMemoryDatabase(ApplicationProvider.getApplicationContext())
        dao = db.noiseLevelDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAll_and_getReadingsSince_returnsInsertedReadings() = runTest {
        dao.insertAll(listOf(
            createTestReading(60.0, hoursAgo(2)),
            createTestReading(70.0, hoursAgo(1)),
            createTestReading(80.0, BASE_TIMESTAMP)
        ))

        val result = dao.getReadingsSince(hoursAgo(3)).first()

        assertEquals(3, result.size)
        // Query returns DESC order: most recent first
        assertEquals(80.0, result[0].decibels, 0.01)
        assertEquals(70.0, result[1].decibels, 0.01)
        assertEquals(60.0, result[2].decibels, 0.01)
    }

    @Test
    fun getReadingsSince_filtersOldReadings() = runTest {
        dao.insertAll(listOf(
            createTestReading(55.0, hoursAgo(2)),   // outside window
            createTestReading(75.0, hoursAgo(0))    // inside window (= BASE_TIMESTAMP)
        ))

        val result = dao.getReadingsSince(hoursAgo(1)).first()

        assertEquals(1, result.size)
        assertEquals(75.0, result[0].decibels, 0.01)
    }

    @Test
    fun getAverageSince_calculatesCorrectly() = runTest {
        dao.insertAll(listOf(
            createTestReading(40.0, hoursAgo(0)),
            createTestReading(60.0, hoursAgo(0)),
            createTestReading(80.0, hoursAgo(0))
        ))

        val avg = dao.getAverageSince(hoursAgo(1))

        assertEquals(60.0, avg!!, 0.01)
    }

    @Test
    fun getPeakSince_returnsMaximum() = runTest {
        dao.insertAll(listOf(
            createTestReading(35.0, hoursAgo(0)),
            createTestReading(78.0, hoursAgo(0)),
            createTestReading(52.0, hoursAgo(0))
        ))

        val peak = dao.getPeakSince(hoursAgo(1))

        assertEquals(78.0, peak!!, 0.01)
    }

    @Test
    fun deleteOlderThan_removesOnlyOldEntries() = runTest {
        dao.insertAll(listOf(
            createTestReading(60.0, hoursAgo(3)),
            createTestReading(65.0, hoursAgo(4)),
            createTestReading(70.0, BASE_TIMESTAMP),
            createTestReading(75.0, BASE_TIMESTAMP - 1_800_000L)  // 30 min ago
        ))

        val deleted = dao.deleteOlderThan(hoursAgo(1))

        assertEquals(2, deleted)
        val remaining = dao.getReadingsSince(0L).first()
        assertEquals(2, remaining.size)
    }

    @Test
    fun deleteAll_clearsDatabase() = runTest {
        dao.insertAll(listOf(
            createTestReading(55.0, hoursAgo(1)),
            createTestReading(60.0, hoursAgo(2)),
            createTestReading(65.0, hoursAgo(3)),
            createTestReading(70.0, hoursAgo(4)),
            createTestReading(75.0, hoursAgo(5))
        ))

        dao.deleteAll()

        val remaining = dao.getReadingsSince(0L).first()
        assertEquals(0, remaining.size)
    }

    @Test
    fun emptyDatabase_returnsNullForAverage() = runTest {
        val avg = dao.getAverageSince(0L)
        assertNull(avg)
    }

    @Test
    fun emptyDatabase_returnsNullForPeak() = runTest {
        val peak = dao.getPeakSince(0L)
        assertNull(peak)
    }

    @Test
    fun flowEmitsNewValue_whenDataChanges() = runTest {
        val flow = dao.getReadingsSince(0L)

        val before = flow.first()
        assertEquals(0, before.size)

        dao.insertAll(listOf(createTestReading(65.0, BASE_TIMESTAMP)))

        val after = flow.first()
        assertEquals(1, after.size)
        assertEquals(65.0, after[0].decibels, 0.01)
    }
}
