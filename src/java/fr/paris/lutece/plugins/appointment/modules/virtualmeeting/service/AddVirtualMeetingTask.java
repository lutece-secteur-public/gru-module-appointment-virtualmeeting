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

import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.AddVirtualMeetingTaskConfig;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeeting;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingTaskInformation;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingTaskInformationHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Workflow task that creates a virtual meeting room for an appointment. On success the room info is persisted in {@code virtualmeeting_room} and can be looked
 * up via {@link VirtualMeetingService}.
 */
public class AddVirtualMeetingTask extends SimpleTask
{
    public static final String MESSAGE_TASK_TITLE = "module.appointment.virtualmeeting.task.addVirtualMeeting.title";

    private static final String MESSAGE_ERROR_NO_PROVIDER = "module.appointment.virtualmeeting.task.information.error.noProvider";
    private static final String MESSAGE_ERROR_ROOM_CREATION_FAILED = "module.appointment.virtualmeeting.task.information.error.roomCreationFailed";

    private static final String BEAN_CONFIG_SERVICE = "appointment-virtualmeeting.addVirtualMeetingTaskService";
    private static final String BEAN_MEETING_SERVICE = "appointment-virtualmeeting.virtualMeetingService";

    @Inject
    @Named( BEAN_CONFIG_SERVICE )
    private ITaskConfigService _taskConfigService;

    @Inject
    @Named( BEAN_MEETING_SERVICE )
    private VirtualMeetingService _virtualMeetingService;

    @Override
    public void init( )
    {
        // No initialization needed
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, locale );
    }

    @Override
    public boolean processTaskWithResult( int nIdResource, String strResourceType, int nIdResourceHistory, HttpServletRequest request, Locale locale,
            User user )
    {
        AddVirtualMeetingTaskConfig config = _taskConfigService.findByPrimaryKey( getId( ) );

        if ( config == null || config.getProvider( ) == null || config.getProvider( ).isEmpty( ) )
        {
            AppLogService.error( "AddVirtualMeetingTask — no provider configured for task {}", getId( ) );
            saveErrorInformation( nIdResourceHistory, MESSAGE_ERROR_NO_PROVIDER, locale );
            return false;
        }

        String strIdResource = String.valueOf( nIdResource );
        VirtualMeeting meeting = _virtualMeetingService.createMeeting( strIdResource, strResourceType, config.getProvider( ) );

        if ( meeting == null )
        {
            saveErrorInformation( nIdResourceHistory, MESSAGE_ERROR_ROOM_CREATION_FAILED, locale );
            return false;
        }

        // Store the host URL in the task information table for workflow history display
        String strHostUrl = _virtualMeetingService.getHostMeetingUrl( meeting, "host-" + nIdResource, "Host", null );

        if ( strHostUrl != null )
        {
            VirtualMeetingTaskInformationHome.create( new VirtualMeetingTaskInformation( nIdResourceHistory, getId( ), strHostUrl ) );
        }

        writeUrlsToResponses( nIdResource, config, meeting );

        return true;
    }

    /**
     * Generate meeting URLs and write them into the configured appointment form fields. Failures are logged but do not fail the task (the meeting room was
     * already created).
     */
    private void writeUrlsToResponses( int nIdAppointment, AddVirtualMeetingTaskConfig config, VirtualMeeting meeting )
    {
        int nIdEntryGuestLink = config.getIdEntryGuestLink( );
        int nIdEntryHostLink = config.getIdEntryHostLink( );

        if ( nIdEntryGuestLink == 0 && nIdEntryHostLink == 0 )
        {
            return;
        }

        try
        {
            AppointmentDTO appointment = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );

            if ( appointment == null )
            {
                AppLogService.error( "AddVirtualMeetingTask — cannot load appointment {}", nIdAppointment );
                return;
            }

            String strDisplayName = appointment.getFirstName( ) + " " + appointment.getLastName( );

            if ( nIdEntryGuestLink > 0 )
            {
                String strGuestUrl = _virtualMeetingService.getGuestMeetingUrl( meeting, appointment.getEmail( ), strDisplayName, null );

                if ( strGuestUrl != null )
                {
                    writeUrlToResponse( nIdAppointment, nIdEntryGuestLink, strGuestUrl );
                }
            }

            if ( nIdEntryHostLink > 0 )
            {
                String strHostUrl = _virtualMeetingService.getHostMeetingUrl( meeting, "host-" + nIdAppointment, "Host", null );

                if ( strHostUrl != null )
                {
                    writeUrlToResponse( nIdAppointment, nIdEntryHostLink, strHostUrl );
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "AddVirtualMeetingTask — failed to write meeting URLs for appointment {}", nIdAppointment, e );
        }
    }

    /**
     * Write a URL value into the form response for the given entry. If a response already exists for this entry, it is updated; otherwise a new response is
     * created and linked to the appointment.
     */
    private void writeUrlToResponse( int nIdAppointment, int nIdEntry, String strUrl )
    {
        List<Response> listResponses = AppointmentResponseService.findListResponse( nIdAppointment );

        for ( Response response : listResponses )
        {
            if ( response.getEntry( ) != null && response.getEntry( ).getIdEntry( ) == nIdEntry )
            {
                response.setResponseValue( strUrl );
                ResponseHome.update( response );
                return;
            }
        }

        // No existing response for this entry — create a new one
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );
        Response response = new Response( );
        response.setEntry( entry );
        response.setResponseValue( strUrl );
        ResponseHome.create( response );
        AppointmentResponseService.insertAppointmentResponse( nIdAppointment, response.getIdResponse( ) );
    }

    private void saveErrorInformation( int nIdResourceHistory, String strMessageKey, Locale locale )
    {
        VirtualMeetingTaskInformation information = new VirtualMeetingTaskInformation( );
        information.setIdHistory( nIdResourceHistory );
        information.setIdTask( getId( ) );
        information.setHostUrl( "" );
        information.setErrorMessage( I18nService.getLocalizedString( strMessageKey, locale ) );
        VirtualMeetingTaskInformationHome.create( information );
    }

    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        VirtualMeetingTaskInformationHome.remove( nIdHistory, getId( ) );
    }
}
