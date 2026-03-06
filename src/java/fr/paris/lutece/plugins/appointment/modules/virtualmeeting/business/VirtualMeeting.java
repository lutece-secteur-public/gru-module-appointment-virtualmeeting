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

import java.sql.Timestamp;

/**
 * Business class representing a virtual meeting room associated with a resource.
 */
public class VirtualMeeting
{
    private int _nIdVirtualMeeting;
    private String _strIdResource;
    private String _strResourceType;
    private String _strRoomName;
    private String _strProvider;
    private Timestamp _dateCreation;

    public int getIdVirtualMeeting( )
    {
        return _nIdVirtualMeeting;
    }

    public void setIdVirtualMeeting( int nIdVirtualMeeting )
    {
        _nIdVirtualMeeting = nIdVirtualMeeting;
    }

    public String getIdResource( )
    {
        return _strIdResource;
    }

    public void setIdResource( String strIdResource )
    {
        _strIdResource = strIdResource;
    }

    public String getResourceType( )
    {
        return _strResourceType;
    }

    public void setResourceType( String strResourceType )
    {
        _strResourceType = strResourceType;
    }

    public String getRoomName( )
    {
        return _strRoomName;
    }

    public void setRoomName( String strRoomName )
    {
        _strRoomName = strRoomName;
    }

    public String getProvider( )
    {
        return _strProvider;
    }

    public void setProvider( String strProvider )
    {
        _strProvider = strProvider;
    }

    public Timestamp getCreationDate( )
    {
        return _dateCreation;
    }

    public void setCreationDate( Timestamp dateCreation )
    {
        _dateCreation = dateCreation;
    }
}
