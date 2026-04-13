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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeeting;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.exception.VirtualMeetingException;
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
     * Create a virtual meeting room for the given resource.
     *
     * @param strIdResource
     *            the resource ID
     * @param strResourceType
     *            the resource type
     * @param strProviderName
     *            the provider name (see {@link IVirtualMeetingProvider#getName()}); if {@code null} or empty, the default provider is used
     * @return the created {@link VirtualMeeting}
     * @throws VirtualMeetingException
     *             if the provider cannot be resolved or the room creation fails
     */
    public VirtualMeeting createMeeting( String strIdResource, String strResourceType, String strProviderName ) throws VirtualMeetingException
    {
        IVirtualMeetingProvider provider = getProvider( strProviderName );

        String strRoomName = ROOM_NAME_PREFIX + strResourceType + "-" + strIdResource;
        int nEmptyTimeout = AppPropertiesService.getPropertyInt( PROPERTY_EMPTY_TIMEOUT, DEFAULT_EMPTY_TIMEOUT );

        Map<String, Object> mapParameters = new HashMap<>( );
        mapParameters.put( IVirtualMeetingProvider.PARAM_ROOM_NAME, strRoomName );
        mapParameters.put( IVirtualMeetingProvider.PARAM_EMPTY_TIMEOUT, nEmptyTimeout );
        mapParameters.put( IVirtualMeetingProvider.PARAM_MAX_PARTICIPANTS, 0 );

        boolean bCreated = provider.createRoom( mapParameters );

        if ( !bCreated )
        {
            throw new VirtualMeetingException( "Failed to create room " + strRoomName, provider.getName( ) );
        }

        VirtualMeeting meeting = new VirtualMeeting( );
        meeting.setIdResource( strIdResource );
        meeting.setResourceType( strResourceType );
        meeting.setRoomName( strRoomName );
        meeting.setProvider( provider.getName( ) );
        meeting.setCreationDate( new Timestamp( System.currentTimeMillis( ) ) );

        return VirtualMeetingHome.create( meeting );
    }

    /**
     * Delete the virtual meeting room associated with the given resource.
     *
     * @param strIdResource
     *            the resource ID
     * @param strResourceType
     *            the resource type
     * @return {@code true} if the room was deleted successfully, {@code false} if no meeting was found for the given resource
     * @throws VirtualMeetingException
     *             if the provider cannot be resolved or the room deletion fails
     */
    public boolean deleteMeeting( String strIdResource, String strResourceType ) throws VirtualMeetingException
    {
        VirtualMeeting meeting = VirtualMeetingHome.findByResourceId( strIdResource, strResourceType );

        if ( meeting == null )
        {
            AppLogService.info( "VirtualMeeting — no meeting found for resource {}/{}", strIdResource, strResourceType );
            return false;
        }

        IVirtualMeetingProvider provider = getProvider( meeting.getProvider( ) );

        Map<String, Object> mapParameters = new HashMap<>( );
        mapParameters.put( IVirtualMeetingProvider.PARAM_ROOM_NAME, meeting.getRoomName( ) );
        provider.deleteRoom( mapParameters );

        VirtualMeetingHome.removeByResourceId( strIdResource, strResourceType );
        return true;
    }

    /**
     * Find the virtual meeting associated with the given resource.
     *
     * @param strIdResource
     *            the resource ID
     * @param strResourceType
     *            the resource type
     * @return the {@link VirtualMeeting}, or {@code null} if none exists
     */
    public VirtualMeeting findByResourceId( String strIdResource, String strResourceType )
    {
        return VirtualMeetingHome.findByResourceId( strIdResource, strResourceType );
    }

    /**
     * Generate a meeting URL for a host (admin) with full participant permissions.
     *
     * @param meeting
     *            the virtual meeting
     * @param strIdentity
     *            the host unique identity (e.g. AdminUser access code)
     * @param strDisplayName
     *            the host display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the meeting URL with a fresh token
     * @throws VirtualMeetingException
     *             if the provider cannot be resolved or URL generation fails
     */
    public String getHostMeetingUrl( VirtualMeeting meeting, String strIdentity, String strDisplayName, Date notBefore ) throws VirtualMeetingException
    {
        IVirtualMeetingProvider provider = getProvider( meeting.getProvider( ) );

        Map<String, Object> mapParameters = new HashMap<>( );
        mapParameters.put( IVirtualMeetingProvider.PARAM_ROOM_NAME, meeting.getRoomName( ) );
        mapParameters.put( IVirtualMeetingProvider.PARAM_IDENTITY, strIdentity );
        mapParameters.put( IVirtualMeetingProvider.PARAM_DISPLAY_NAME, strDisplayName );
        mapParameters.put( IVirtualMeetingProvider.PARAM_NOT_BEFORE, notBefore );

        return provider.getParticipantMeetingUrl( mapParameters );
    }

    /**
     * Generate a meeting URL for a guest with full participant permissions.
     *
     * @param meeting
     *            the virtual meeting
     * @param strIdentity
     *            the guest unique identity (e.g. email or GUID)
     * @param strDisplayName
     *            the guest display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the meeting URL with a fresh token
     * @throws VirtualMeetingException
     *             if the provider cannot be resolved or URL generation fails
     */
    public String getGuestMeetingUrl( VirtualMeeting meeting, String strIdentity, String strDisplayName, Date notBefore ) throws VirtualMeetingException
    {
        IVirtualMeetingProvider provider = getProvider( meeting.getProvider( ) );

        Map<String, Object> mapParameters = new HashMap<>( );
        mapParameters.put( IVirtualMeetingProvider.PARAM_ROOM_NAME, meeting.getRoomName( ) );
        mapParameters.put( IVirtualMeetingProvider.PARAM_IDENTITY, strIdentity );
        mapParameters.put( IVirtualMeetingProvider.PARAM_DISPLAY_NAME, strDisplayName );
        mapParameters.put( IVirtualMeetingProvider.PARAM_NOT_BEFORE, notBefore );

        return provider.getParticipantMeetingUrl( mapParameters );
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
     * @return the matching provider (never {@code null})
     * @throws VirtualMeetingException
     *             if no matching or default provider is found
     */
    public IVirtualMeetingProvider getProvider( String strProviderName ) throws VirtualMeetingException
    {
        List<IVirtualMeetingProvider> providers = SpringContextService.getBeansOfType( IVirtualMeetingProvider.class );

        if ( strProviderName != null && !strProviderName.isEmpty( ) )
        {
            return providers.stream( ).filter( p -> strProviderName.equals( p.getName( ) ) ).findFirst( )
                    .orElseThrow( ( ) -> new VirtualMeetingException( "No provider found for name: " + strProviderName ) );
        }

        // Fallback: return the default provider
        return providers.stream( ).filter( IVirtualMeetingProvider::isDefault ).findFirst( )
                .orElseThrow( ( ) -> new VirtualMeetingException( "No default virtual meeting provider configured" ) );
    }
}
