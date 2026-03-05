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
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.provider;

import java.util.Date;

/**
 * Technology-agnostic interface for virtual meeting operations. Implement this interface to integrate a specific video-conferencing backend (LiveKit, Jitsi,
 * etc.).
 *
 * <p>
 * Follows the same provider pattern as {@code IFileStoreServiceProvider} in lutece-core: each implementation declares a unique {@link #getName() name} and an
 * optional {@link #isDefault() default} flag used for automatic selection.
 * </p>
 */
public interface IVirtualMeetingProvider
{
    /**
     * Get the unique name of this provider (e.g. "livekit", "jitsi").
     *
     * @return the provider name
     */
    String getName( );

    /**
     * Whether this provider should be used when no explicit name is configured.
     *
     * @return {@code true} if this is the default provider
     */
    boolean isDefault( );

    /**
     * Create a new meeting room.
     *
     * @param strRoomName
     *            the room name
     * @param nEmptyTimeoutSeconds
     *            seconds to keep the room alive after the last participant leaves (0 = server default)
     * @param nMaxParticipants
     *            maximum number of participants (0 = unlimited)
     * @return {@code true} if the room was created successfully
     */
    boolean createRoom( String strRoomName, int nEmptyTimeoutSeconds, int nMaxParticipants );

    /**
     * Delete an existing room.
     *
     * @param strRoomName
     *            the room name
     * @return {@code true} if the room was deleted successfully
     */
    boolean deleteRoom( String strRoomName );

    /**
     * Generate a participant token with full publish and subscribe permissions.
     *
     * @param strRoomName
     *            the room name
     * @param strIdentity
     *            the participant unique identity
     * @param strName
     *            the participant display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the authentication token string
     */
    String generateParticipantToken( String strRoomName, String strIdentity, String strName, Date notBefore );

    /**
     * Generate a viewer token with subscribe-only permissions (no publish).
     *
     * @param strRoomName
     *            the room name
     * @param strIdentity
     *            the viewer unique identity
     * @param strName
     *            the viewer display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the authentication token string
     */
    String generateViewerToken( String strRoomName, String strIdentity, String strName, Date notBefore );

    /**
     * Get the full meeting join URL for a participant.
     * <p>
     * Providers that generate opaque tokens (e.g. JWT) should override this to embed the token into a front-end URL. Providers that already return a complete
     * URL from {@link #generateParticipantToken} can rely on the default implementation.
     * </p>
     *
     * @param strRoomName
     *            the room name
     * @param strIdentity
     *            the participant unique identity
     * @param strName
     *            the participant display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the full meeting URL ready for the participant to join
     */
    default String getParticipantMeetingUrl( String strRoomName, String strIdentity, String strName, Date notBefore )
    {
        return generateParticipantToken( strRoomName, strIdentity, strName, notBefore );
    }

    /**
     * Get the full meeting join URL for a viewer.
     * <p>
     * Providers that generate opaque tokens (e.g. JWT) should override this to embed the token into a front-end URL. Providers that already return a complete
     * URL from {@link #generateViewerToken} can rely on the default implementation.
     * </p>
     *
     * @param strRoomName
     *            the room name
     * @param strIdentity
     *            the viewer unique identity
     * @param strName
     *            the viewer display name
     * @param notBefore
     *            the earliest date/time the token becomes valid ({@code null} for immediate validity)
     * @return the full meeting URL ready for the viewer to join
     */
    default String getViewerMeetingUrl( String strRoomName, String strIdentity, String strName, Date notBefore )
    {
        return generateViewerToken( strRoomName, strIdentity, strName, notBefore );
    }
}
