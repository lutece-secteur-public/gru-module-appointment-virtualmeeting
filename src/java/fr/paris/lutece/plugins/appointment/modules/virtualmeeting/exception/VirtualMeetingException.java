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
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.exception;

/**
 * Checked exception thrown when a virtual meeting provider operation fails. Carries the provider name so that callers can include it in logs and error messages.
 */
public class VirtualMeetingException extends Exception
{
    private static final long serialVersionUID = 1L;

    private final String _strProviderName;

    /**
     * Creates a new instance with a message only (no provider context).
     *
     * @param strMessage
     *            the error message
     */
    public VirtualMeetingException( String strMessage )
    {
        super( strMessage );
        _strProviderName = null;
    }

    /**
     * Creates a new instance with a message and a cause (no provider context).
     *
     * @param strMessage
     *            the error message
     * @param cause
     *            the underlying cause
     */
    public VirtualMeetingException( String strMessage, Exception cause )
    {
        super( strMessage, cause );
        _strProviderName = null;
    }

    /**
     * Creates a new instance with a message and the name of the provider that raised the error.
     *
     * @param strMessage
     *            the error message
     * @param strProviderName
     *            the name of the provider (e.g. "livekit", "jitsi")
     */
    public VirtualMeetingException( String strMessage, String strProviderName )
    {
        super( strMessage );
        _strProviderName = strProviderName;
    }

    /**
     * Creates a new instance with a message, provider name, and underlying cause.
     *
     * @param strMessage
     *            the error message
     * @param strProviderName
     *            the name of the provider (e.g. "livekit", "jitsi")
     * @param cause
     *            the underlying cause
     */
    public VirtualMeetingException( String strMessage, String strProviderName, Exception cause )
    {
        super( strMessage, cause );
        _strProviderName = strProviderName;
    }

    /**
     * Get the name of the provider that raised the error.
     *
     * @return the provider name, or {@code null} if the error is not tied to a specific provider (e.g. "no provider found")
     */
    public String getProviderName( )
    {
        return _strProviderName;
    }
}
