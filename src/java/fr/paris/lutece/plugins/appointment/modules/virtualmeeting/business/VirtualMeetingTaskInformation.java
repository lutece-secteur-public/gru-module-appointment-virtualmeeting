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

/**
 * Business class for task information stored alongside workflow history entries. Holds the host meeting URL generated when a virtual meeting is created.
 */
public class VirtualMeetingTaskInformation
{
    private int _nIdHistory;
    private int _nIdTask;
    private String _strHostUrl;
    private String _strErrorMessage;

    /**
     * Default constructor.
     */
    public VirtualMeetingTaskInformation( )
    {
    }

    /**
     * Constructor with all fields.
     *
     * @param nIdHistory
     *            the workflow history id
     * @param nIdTask
     *            the task id
     * @param strHostUrl
     *            the host meeting URL
     */
    public VirtualMeetingTaskInformation( int nIdHistory, int nIdTask, String strHostUrl )
    {
        _nIdHistory = nIdHistory;
        _nIdTask = nIdTask;
        _strHostUrl = strHostUrl;
    }

    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    public int getIdTask( )
    {
        return _nIdTask;
    }

    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    public String getHostUrl( )
    {
        return _strHostUrl;
    }

    public void setHostUrl( String strHostUrl )
    {
        _strHostUrl = strHostUrl;
    }

    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    public void setErrorMessage( String strErrorMessage )
    {
        _strErrorMessage = strErrorMessage;
    }
}
