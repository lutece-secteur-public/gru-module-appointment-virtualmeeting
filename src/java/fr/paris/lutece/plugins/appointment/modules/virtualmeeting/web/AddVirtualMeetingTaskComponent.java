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
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.AddVirtualMeetingTaskConfig;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingTaskInformation;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingTaskInformationHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.provider.IVirtualMeetingProvider;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Web component for the "add virtual meeting" workflow task. Handles configuration form (provider selection) and task history information display.
 */
public class AddVirtualMeetingTaskComponent extends AbstractTaskComponent
{
    // Templates
    private static final String TEMPLATE_TASK_CONFIG = "admin/plugins/workflow/modules/virtualmeeting/task_addvirtualmeeting_config.html";
    private static final String TEMPLATE_TASK_INFORMATION = "admin/plugins/workflow/modules/virtualmeeting/task_addvirtualmeeting_information.html";

    // Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_PROVIDER_LIST = "provider_list";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_AGENT_URL = "agent_url";
    private static final String MARK_ERROR_MESSAGE = "error_message";

    // Beans
    private static final String BEAN_ACTION_SERVICE = "workflow.actionService";

    // Constants
    private static final String RESOURCE_TYPE_APPOINTMENT = "APPOINTMENT_FORM";

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        AddVirtualMeetingTaskConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config == null )
        {
            config = new AddVirtualMeetingTaskConfig( );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_PROVIDER_LIST, getAvailableProviders( ) );
        model.put( MARK_ENTRY_LIST, getFormEntries( task ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CONFIG, locale, model );
        return template.getHtml( );
    }

    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        return super.validateConfig( config, request );
    }

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        VirtualMeetingTaskInformation information = VirtualMeetingTaskInformationHome.find( nIdHistory, task.getId( ) );

        if ( information != null )
        {
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_AGENT_URL, information.getAgentUrl( ) );
            model.put( MARK_ERROR_MESSAGE, information.getErrorMessage( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_INFORMATION, locale, model );
            return template.getHtml( );
        }

        return null;
    }

    /**
     * Build a ReferenceList of all registered IVirtualMeetingProvider implementations, keyed by {@link IVirtualMeetingProvider#getName()}.
     */
    private ReferenceList getAvailableProviders( )
    {
        ReferenceList list = new ReferenceList( );
        List<IVirtualMeetingProvider> providers = SpringContextService.getBeansOfType( IVirtualMeetingProvider.class );

        for ( IVirtualMeetingProvider provider : providers )
        {
            list.addItem( provider.getName( ), provider.getName( ) );
        }

        return list;
    }

    /**
     * Build a ReferenceList of form entries from the appointment form linked to the task's workflow. The first item is a blank option (code "0") for "not
     * configured".
     */
    private ReferenceList getFormEntries( ITask task )
    {
        ReferenceList list = new ReferenceList( );
        list.addItem( "0", "" );

        try
        {
            IActionService actionService = SpringContextService.getBean( BEAN_ACTION_SERVICE );
            Action action = actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( action == null || action.getWorkflow( ) == null )
            {
                return list;
            }

            int nIdWorkflow = action.getWorkflow( ).getId( );

            for ( Form form : FormHome.findAllForms( ) )
            {
                if ( form.getIdWorkflow( ) == nIdWorkflow )
                {
                    EntryFilter filter = new EntryFilter( );
                    filter.setIdResource( form.getIdForm( ) );
                    filter.setResourceType( RESOURCE_TYPE_APPOINTMENT );

                    for ( Entry entry : EntryHome.getEntryList( filter ) )
                    {
                        list.addItem( String.valueOf( entry.getIdEntry( ) ), entry.getTitle( ) );
                    }
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "AddVirtualMeetingTaskComponent — failed to resolve form entries", e );
        }

        return list;
    }
}
