/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.mesh.provisionerstates;

import no.nordicsemi.android.mesh.logger.MeshLogger;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import no.nordicsemi.android.mesh.InternalTransportCallbacks;
import no.nordicsemi.android.mesh.MeshManagerApi;
import no.nordicsemi.android.mesh.MeshProvisioningStatusCallbacks;

public class ProvisioningInviteState extends ProvisioningState {

    private final String TAG = ProvisioningInviteState.class.getSimpleName();
    private final UnprovisionedMeshNode node;
    private final int attentionTimer;
    private final MeshProvisioningStatusCallbacks mStatusCallbacks;
    private final InternalTransportCallbacks mInternalTransportCallbacks;

    /**
     * Constructs the provisioning invite state.
     *
     * @param node                        {@link UnprovisionedMeshNode} node.
     * @param attentionTimer              Duration of attention timer.
     * @param internalTransportCallbacks  {@link InternalTransportCallbacks}
     *                                    callbacks.
     * @param provisioningStatusCallbacks {@link MeshProvisioningStatusCallbacks}
     *                                    callbacks.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ProvisioningInviteState(final UnprovisionedMeshNode node,
            final int attentionTimer,
            final InternalTransportCallbacks internalTransportCallbacks,
            final MeshProvisioningStatusCallbacks provisioningStatusCallbacks) {
        super();
        this.node = node;
        this.attentionTimer = attentionTimer;
        this.mStatusCallbacks = provisioningStatusCallbacks;
        this.mInternalTransportCallbacks = internalTransportCallbacks;
    }

    @Override
    public State getState() {
        return State.PROVISIONING_INVITE;
    }

    @Override
    public void executeSend() {
        final byte[] invitePDU = createInvitePDU();
        // We store the provisioning invite pdu to be used when generating confirmation
        // inputs
        node.setProvisioningInvitePdu(invitePDU);
        mStatusCallbacks.onProvisioningStateChanged(node, States.PROVISIONING_INVITE, invitePDU);
        mInternalTransportCallbacks.sendProvisioningPdu(node, invitePDU);
    }

    @Override
    public boolean parseData(@NonNull final byte[] data) {
        return true;
    }

    /**
     * Generates the invitePDU for provisioning based on the attention timer
     * provided by the user.
     */
    private byte[] createInvitePDU() {

        final byte[] data = new byte[3];
        data[0] = MeshManagerApi.PDU_TYPE_PROVISIONING; // Provisioning Opcode;
        // noinspection ConstantConditions
        data[1] = TYPE_PROVISIONING_INVITE; // PDU type in
        data[2] = (byte) attentionTimer;
        MeshLogger.debug(TAG, "attentionTimer: " + attentionTimer);

        return data;
    }
}
