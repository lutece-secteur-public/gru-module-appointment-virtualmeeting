/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business;

import java.io.Serializable;
import java.util.Set;

/**
 * Holder for the guest and host entry IDs resolved from a workflow's virtual meeting task configurations.
 */
public class VirtualMeetingEntryIds implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final Set<Integer> _setGuestEntryIds;
    private final Set<Integer> _setHostEntryIds;

    /**
     * Constructor
     *
     * @param setGuestEntryIds
     *            the set of entry IDs configured for guest links
     * @param setHostEntryIds
     *            the set of entry IDs configured for host links
     */
    public VirtualMeetingEntryIds( Set<Integer> setGuestEntryIds, Set<Integer> setHostEntryIds )
    {
        _setGuestEntryIds = setGuestEntryIds;
        _setHostEntryIds = setHostEntryIds;
    }

    /**
     * Returns the guest entry IDs
     *
     * @return the guest entry IDs
     */
    public Set<Integer> getGuestEntryIds( )
    {
        return _setGuestEntryIds;
    }

    /**
     * Returns the host entry IDs
     *
     * @return the host entry IDs
     */
    public Set<Integer> getHostEntryIds( )
    {
        return _setHostEntryIds;
    }
}
