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
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeeting;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.provider.IVirtualMeetingProvider;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Service orchestrating virtual meeting operations: room lifecycle and token-based URL generation.
 */
public class VirtualMeetingService
{
    private static final String PROPERTY_EMPTY_TIMEOUT = "virtualmeeting.room.emptyTimeout";

    private static final int DEFAULT_EMPTY_TIMEOUT = 300;

    private static final String ROOM_NAME_PREFIX = "vm-";

    /**
     * Create a virtual meeting room for the given appointment.
     *
     * @param nIdAppointment
     *            the appointment ID
     * @param strProviderName
     *            the provider name (see {@link IVirtualMeetingProvider#getName()}); if {@code null} or empty, the default provider is used
     * @return the created {@link VirtualMeeting}, or {@code null} on failure
     */
    public VirtualMeeting createMeeting( int nIdAppointment, String strProviderName )
    {
        IVirtualMeetingProvider provider = getProvider( strProviderName );

        if ( provider == null )
        {
            AppLogService.error( "VirtualMeeting — no provider found for name: {}", strProviderName );
            return null;
        }

        String strRoomName = ROOM_NAME_PREFIX + nIdAppointment;
        int nEmptyTimeout = AppPropertiesService.getPropertyInt( PROPERTY_EMPTY_TIMEOUT, DEFAULT_EMPTY_TIMEOUT );

        boolean bCreated = provider.createRoom( strRoomName, nEmptyTimeout, 0 );

        if ( !bCreated )
        {
            AppLogService.error( "VirtualMeeting — failed to create room {} via provider '{}'", strRoomName, provider.getName( ) );
            return null;
        }

        VirtualMeeting meeting = new VirtualMeeting( );
        meeting.setIdAppointment( nIdAppointment );
        meeting.setRoomName( strRoomName );
        meeting.setProvider( provider.getName( ) );
        meeting.setCreationDate( new Timestamp( System.currentTimeMillis( ) ) );

        return VirtualMeetingHome.create( meeting );
    }

    /**
     * Delete the virtual meeting room associated with the given appointment.
     *
     * @param nIdAppointment
     *            the appointment ID
     * @return {@code true} if the room was deleted successfully
     */
    public boolean deleteMeeting( int nIdAppointment )
    {
        VirtualMeeting meeting = VirtualMeetingHome.findByAppointmentId( nIdAppointment );

        if ( meeting == null )
        {
            AppLogService.info( "VirtualMeeting — no meeting found for appointment {}", nIdAppointment );
            return false;
        }

        IVirtualMeetingProvider provider = getProvider( meeting.getProvider( ) );

        if ( provider != null )
        {
            provider.deleteRoom( meeting.getRoomName( ) );
        }
        else
        {
            AppLogService.error( "VirtualMeeting — no provider found for name: '{}'. Removing DB record only.", meeting.getProvider( ) );
        }

        VirtualMeetingHome.removeByAppointmentId( nIdAppointment );
        return true;
    }

    /**
     * Find the virtual meeting associated with the given appointment.
     *
     * @param nIdAppointment
     *            the appointment ID
     * @return the {@link VirtualMeeting}, or {@code null} if none exists
     */
    public VirtualMeeting findByAppointmentId( int nIdAppointment )
    {
        return VirtualMeetingHome.findByAppointmentId( nIdAppointment );
    }

    /**
     * Generate a meeting URL for an agent (admin) with full participant permissions.
     *
     * @param meeting
     *            the virtual meeting
     * @param strIdentity
     *            the agent unique identity (e.g. AdminUser access code)
     * @param strDisplayName
     *            the agent display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the meeting URL with a fresh token
     */
    public String getAgentMeetingUrl( VirtualMeeting meeting, String strIdentity, String strDisplayName, Date notBefore )
    {
        IVirtualMeetingProvider provider = getProvider( meeting.getProvider( ) );

        if ( provider == null )
        {
            return null;
        }

        return provider.getParticipantMeetingUrl( meeting.getRoomName( ), strIdentity, strDisplayName, notBefore );
    }

    /**
     * Generate a meeting URL for a user with full participant permissions.
     *
     * @param meeting
     *            the virtual meeting
     * @param strIdentity
     *            the user unique identity (e.g. email or GUID)
     * @param strDisplayName
     *            the user display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the meeting URL with a fresh token
     */
    public String getUserMeetingUrl( VirtualMeeting meeting, String strIdentity, String strDisplayName, Date notBefore )
    {
        IVirtualMeetingProvider provider = getProvider( meeting.getProvider( ) );

        if ( provider == null )
        {
            return null;
        }

        return provider.getParticipantMeetingUrl( meeting.getRoomName( ), strIdentity, strDisplayName, notBefore );
    }

    /**
     * Resolve a provider by its {@link IVirtualMeetingProvider#getName() name}. If {@code strProviderName} is {@code null} or empty, the default provider
     * ({@link IVirtualMeetingProvider#isDefault()}) is returned.
     *
     * <p>
     * Follows the same discovery pattern as {@code FileService} in lutece-core.
     * </p>
     *
     * @param strProviderName
     *            the provider name, or {@code null} for the default
     * @return the matching provider, or {@code null} if none found
     */
    public IVirtualMeetingProvider getProvider( String strProviderName )
    {
        List<IVirtualMeetingProvider> providers = SpringContextService.getBeansOfType( IVirtualMeetingProvider.class );

        if ( strProviderName != null && !strProviderName.isEmpty( ) )
        {
            return providers.stream( ).filter( p -> strProviderName.equals( p.getName( ) ) ).findFirst( ).orElse( null );
        }

        // Fallback: return the default provider
        return providers.stream( ).filter( IVirtualMeetingProvider::isDefault ).findFirst( ).orElse( null );
    }
}
