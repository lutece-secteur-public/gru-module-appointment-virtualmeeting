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

import java.sql.Statement;
import java.sql.Timestamp;

import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Data Access methods for {@link VirtualMeeting} objects.
 */
public class VirtualMeetingDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO virtualmeeting_room ( id_appointment, room_name, provider, creation_date ) VALUES ( ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM virtualmeeting_room WHERE id_virtualmeeting = ?";
    private static final String SQL_QUERY_DELETE_BY_APPOINTMENT = "DELETE FROM virtualmeeting_room WHERE id_appointment = ?";
    private static final String SQL_QUERY_SELECT_BY_APPOINTMENT = "SELECT id_virtualmeeting, id_appointment, room_name, provider, creation_date FROM virtualmeeting_room WHERE id_appointment = ?";

    public void insert( VirtualMeeting virtualMeeting )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, virtualMeeting.getIdAppointment( ) );
            daoUtil.setString( nIndex++, virtualMeeting.getRoomName( ) );
            daoUtil.setString( nIndex++, virtualMeeting.getProvider( ) );
            daoUtil.setTimestamp( nIndex, virtualMeeting.getCreationDate( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                virtualMeeting.setIdVirtualMeeting( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    public void delete( int nIdVirtualMeeting )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nIdVirtualMeeting );
            daoUtil.executeUpdate( );
        }
    }

    public void deleteByAppointmentId( int nIdAppointment )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_APPOINTMENT ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeUpdate( );
        }
    }

    public VirtualMeeting findByAppointmentId( int nIdAppointment )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_APPOINTMENT ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                return loadFromDaoUtil( daoUtil );
            }

            return null;
        }
    }

    private VirtualMeeting loadFromDaoUtil( DAOUtil daoUtil )
    {
        VirtualMeeting virtualMeeting = new VirtualMeeting( );
        int nIndex = 1;

        virtualMeeting.setIdVirtualMeeting( daoUtil.getInt( nIndex++ ) );
        virtualMeeting.setIdAppointment( daoUtil.getInt( nIndex++ ) );
        virtualMeeting.setRoomName( daoUtil.getString( nIndex++ ) );
        virtualMeeting.setProvider( daoUtil.getString( nIndex++ ) );
        virtualMeeting.setCreationDate( daoUtil.getTimestamp( nIndex ) );

        return virtualMeeting;
    }
}
