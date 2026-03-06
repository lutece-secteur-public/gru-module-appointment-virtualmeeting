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

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

/**
 * Business class AddVirtualMeetingTaskConfig Contains all configurations task.
 */
public class AddVirtualMeetingTaskConfig extends TaskConfig
{
    // Variables declaration
    private String _strProvider;
    private int _nIdEntryGuestLink;
    private int _nIdEntryHostLink;

    /**
     * Returns the Provider
     * 
     * @return The Provider
     */
    public String getProvider( )
    {
        return _strProvider;
    }

    /**
     * Sets the Provider
     * 
     * @param strProvider
     *            The Provider
     */
    public void setProvider( String strProvider )
    {
        _strProvider = strProvider;
    }

    /**
     * Returns the IdEntryGuestLink
     *
     * @return The IdEntryGuestLink
     */
    public int getIdEntryGuestLink( )
    {
        return _nIdEntryGuestLink;
    }

    /**
     * Sets the IdEntryGuestLink
     *
     * @param nIdEntryGuestLink
     *            The IdEntryGuestLink
     */
    public void setIdEntryGuestLink( int nIdEntryGuestLink )
    {
        _nIdEntryGuestLink = nIdEntryGuestLink;
    }

    /**
     * Returns the IdEntryHostLink
     *
     * @return The IdEntryHostLink
     */
    public int getIdEntryHostLink( )
    {
        return _nIdEntryHostLink;
    }

    /**
     * Sets the IdEntryHostLink
     *
     * @param nIdEntryHostLink
     *            The IdEntryHostLink
     */
    public void setIdEntryHostLink( int nIdEntryHostLink )
    {
        _nIdEntryHostLink = nIdEntryHostLink;
    }
}
