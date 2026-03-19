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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.AddVirtualMeetingTaskConfig;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingEntryIds;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.addon.IAppointmentAddonService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Appointment addon service that renders virtual meeting links (guest/host) as clickable URLs in the back-office appointment detail view.
 */
public class VirtualMeetingAppointmentAddonService implements IAppointmentAddonService
{
    // Template
    private static final String TEMPLATE_ADDON = "admin/plugins/workflow/modules/virtualmeeting/appointment_virtualmeeting_addon.html";

    // Markers
    private static final String MARK_GUEST_URL = "guest_url";
    private static final String MARK_HOST_URL = "host_url";
    private static final String MARK_GUEST_ENTRY_IDS = "guest_entry_ids";
    private static final String MARK_HOST_ENTRY_IDS = "host_entry_ids";

    // Beans
    private static final String BEAN_ACTION_SERVICE = "workflow.actionService";
    private static final String BEAN_TASK_SERVICE = "workflow.taskService";
    private static final String BEAN_CONFIG_SERVICE = "appointment-virtualmeeting.addVirtualMeetingTaskService";
    private static final String BEAN_CACHE_SERVICE = "appointment-virtualmeeting.entryIdsCacheService";

    private IActionService _actionService;
    private ITaskService _taskService;
    private ITaskConfigService _taskConfigService;
    private VirtualMeetingEntryIdsCacheService _cacheService;

    private IActionService getActionService( )
    {
        if ( _actionService == null )
        {
            _actionService = SpringContextService.getBean( BEAN_ACTION_SERVICE );
        }

        return _actionService;
    }

    private ITaskService getTaskService( )
    {
        if ( _taskService == null )
        {
            _taskService = SpringContextService.getBean( BEAN_TASK_SERVICE );
        }

        return _taskService;
    }

    private ITaskConfigService getTaskConfigService( )
    {
        if ( _taskConfigService == null )
        {
            _taskConfigService = SpringContextService.getBean( BEAN_CONFIG_SERVICE );
        }

        return _taskConfigService;
    }

    private VirtualMeetingEntryIdsCacheService getCacheService( )
    {
        if ( _cacheService == null )
        {
            _cacheService = SpringContextService.getBean( BEAN_CACHE_SERVICE );
        }

        return _cacheService;
    }

    @Override
    public String getAppointmentAddOn( int nIdAppointment, Locale locale )
    {
        try
        {
            AppointmentDTO appointment = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );

            if ( appointment == null )
            {
                return "";
            }

            Form form = FormHome.findByPrimaryKey( appointment.getIdForm( ) );

            if ( form == null || form.getIdWorkflow( ) <= 0 )
            {
                return "";
            }

            // Resolve entry IDs from workflow config (cached)
            VirtualMeetingEntryIds entryIds = resolveEntryIds( form.getIdWorkflow( ), locale );
            Set<Integer> setGuestEntryIds = entryIds.getGuestEntryIds( );
            Set<Integer> setHostEntryIds = entryIds.getHostEntryIds( );

            if ( setGuestEntryIds.isEmpty( ) && setHostEntryIds.isEmpty( ) )
            {
                return "";
            }

            // Look up the response values for the configured entry IDs
            String strGuestUrl = null;
            String strHostUrl = null;
            List<Response> listResponses = AppointmentResponseService.findListResponse( nIdAppointment );

            for ( Response response : listResponses )
            {
                if ( response.getEntry( ) == null )
                {
                    continue;
                }

                int nIdEntry = response.getEntry( ).getIdEntry( );

                if ( strGuestUrl == null && setGuestEntryIds.contains( nIdEntry ) && response.getResponseValue( ) != null
                        && !response.getResponseValue( ).isEmpty( ) )
                {
                    strGuestUrl = response.getResponseValue( );
                }

                if ( strHostUrl == null && setHostEntryIds.contains( nIdEntry ) && response.getResponseValue( ) != null
                        && !response.getResponseValue( ).isEmpty( ) )
                {
                    strHostUrl = response.getResponseValue( );
                }

                if ( strGuestUrl != null && strHostUrl != null )
                {
                    break;
                }
            }

            if ( strGuestUrl == null && strHostUrl == null )
            {
                return "";
            }

            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_GUEST_URL, strGuestUrl );
            model.put( MARK_HOST_URL, strHostUrl );
            model.put( MARK_GUEST_ENTRY_IDS, setGuestEntryIds );
            model.put( MARK_HOST_ENTRY_IDS, setHostEntryIds );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADDON, locale, model );
            return template.getHtml( );
        }
        catch( Exception e )
        {
            AppLogService.error( "VirtualMeetingAppointmentAddonService — failed to build addon for appointment {}", nIdAppointment, e );
            return "";
        }
    }

    /**
     * Resolves the guest and host entry IDs for a given workflow by scanning its actions, tasks, and task configs.
     * Results are cached by workflow ID to avoid repeated DAO lookups.
     *
     * @param nIdWorkflow
     *            the workflow ID
     * @param locale
     *            the locale for task resolution
     * @return the resolved entry IDs
     */
    private VirtualMeetingEntryIds resolveEntryIds( int nIdWorkflow, Locale locale )
    {
        // Check cache first
        if ( getCacheService( ).isCacheEnable( ) )
        {
            VirtualMeetingEntryIds cached = getCacheService( ).getEntryIds( nIdWorkflow );

            if ( cached != null )
            {
                return cached;
            }
        }

        // Scan workflow actions → tasks → configs to collect entry IDs
        Set<Integer> setGuestEntryIds = new HashSet<>( );
        Set<Integer> setHostEntryIds = new HashSet<>( );

        ActionFilter filter = new ActionFilter( );
        filter.setIdWorkflow( nIdWorkflow );
        List<Action> listActions = getActionService( ).getListActionByFilter( filter );

        for ( Action action : listActions )
        {
            List<ITask> listTasks = getTaskService( ).getListTaskByIdAction( action.getId( ), locale );

            for ( ITask task : listTasks )
            {
                AddVirtualMeetingTaskConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

                if ( config != null )
                {
                    if ( config.getIdEntryGuestLink( ) > 0 )
                    {
                        setGuestEntryIds.add( config.getIdEntryGuestLink( ) );
                    }

                    if ( config.getIdEntryHostLink( ) > 0 )
                    {
                        setHostEntryIds.add( config.getIdEntryHostLink( ) );
                    }
                }
            }
        }

        VirtualMeetingEntryIds entryIds = new VirtualMeetingEntryIds( setGuestEntryIds, setHostEntryIds );

        // Store in cache
        if ( getCacheService( ).isCacheEnable( ) )
        {
            getCacheService( ).putEntryIds( nIdWorkflow, entryIds );
        }

        return entryIds;
    }
}
