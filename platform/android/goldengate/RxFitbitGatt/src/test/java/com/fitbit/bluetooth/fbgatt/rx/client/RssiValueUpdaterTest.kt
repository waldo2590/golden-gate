package com.fitbit.bluetooth.fbgatt.rx.client

import com.fitbit.bluetooth.fbgatt.GattState
import com.fitbit.bluetooth.fbgatt.TransactionResult
import com.fitbit.bluetooth.fbgatt.rx.mockGattConnection
import com.fitbit.bluetooth.fbgatt.rx.mockGattTransactionCompletion
import com.fitbit.bluetooth.fbgatt.tx.ReadRssiTransaction
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.Test

class RssiValueUpdaterTest {

    private val mockReadRssiTransaction: ReadRssiTransaction = mock()
    private val mockGattClientConnectionStateSetter: GattClientConnectionStateSetter = mock()

    private val updater = RssiValueUpdater(
        { mockReadRssiTransaction },
        mockGattClientConnectionStateSetter
    )

    @Test
    fun shouldReturnGattConnectionIfTransactionSucceeds() {
        mockTransactionSuccess()

        updater.update(mockGattConnection)
            .test()
            .assertValue { it== mockGattConnection}

        verify(mockGattClientConnectionStateSetter, never()).set(any(), any())
    }

    @Test
    fun shouldReturnGattConnectionAndResetGattStateIfTransactionFails() {
        mockTransactionFailure()
        whenever(mockGattClientConnectionStateSetter.set(mockGattConnection, GattState.IDLE)).thenReturn(Completable.complete())

        updater.update(mockGattConnection)
            .test()
            .assertValue { it== mockGattConnection}

        verify(mockGattClientConnectionStateSetter).set(mockGattConnection, GattState.IDLE)
    }

    private fun mockTransactionSuccess() =
        mockReadRssiTransaction.mockGattTransactionCompletion(TransactionResult.TransactionResultStatus.SUCCESS)

    private fun mockTransactionFailure() =
        mockReadRssiTransaction.mockGattTransactionCompletion(TransactionResult.TransactionResultStatus.FAILURE)

}