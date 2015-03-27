package org.sakaiproject.gradebookng.tool.panels;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.sakaiproject.gradebookng.business.GradebookNgBusinessService;
import org.sakaiproject.gradebookng.tool.model.GradeInfo;
import org.sakaiproject.gradebookng.tool.model.StudentGradeInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * The panel for the cell of a grade item
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class GradeItemCellPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	@SpringBean(name="org.sakaiproject.gradebookng.business.GradebookNgBusinessService")
	protected GradebookNgBusinessService businessService;

	//todo wrap this in a model as per http://wicket.apache.org/guide/guide/bestpractices.html#bestpractices_6
	public GradeItemCellPanel(String id, final Long assignmentId, final StudentGradeInfo studentGrades) {
		super(id);
		
		//TODO harden this
		GradeInfo gradeInfo = studentGrades.getGrades().get(assignmentId);
		
		//get grade
		String formattedGrade = this.formatGrade(gradeInfo.getGrade());
						
		AjaxEditableLabel<String> grade = new AjaxEditableLabel<String>("grade", new Model<String>(formattedGrade)) {
			
			private static final long serialVersionUID = 1L;
			

			@Override
			protected String defaultNullLabel() {
				return "-"; //TODO this is temporary, they need something to click
			}
			
			@Override
			protected void onSubmit(final AjaxRequestTarget target) {
				super.onSubmit(target);
				String newGrade = this.getEditor().getValue();
				
				boolean result = businessService.saveGrade(assignmentId, studentGrades.getStudentUuid(), newGrade);
				//TODO do something with the result
				
				//format it for display
				String formattedGrade = formatGrade(newGrade);
				
				this.getLabel().setDefaultModelObject(formattedGrade);
				
				target.add(this);
			}
			
			@Override
			protected void updateLabelAjaxAttributes(AjaxRequestAttributes attributes) {
				//when switching from editor to label
				Map<String,Object> extraParameters = attributes.getExtraParameters();
				extraParameters.put("assignmentId", assignmentId);
				extraParameters.put("studentUuid", studentGrades.getStudentUuid());

        AjaxCallListener myAjaxCallListener = new AjaxCallListener() {
          @Override
          public CharSequence getBeforeSendHandler(Component component) {
            return "console.log(\"updateLabelAjaxAttributes::I\'m executed before the firing of the Ajax call\")";
          }

          @Override
          public CharSequence getSuccessHandler(Component component) {
            return "console.log(\"updateLabelAjaxAttributes::I\'m executed on successful Ajax call\")";
          }

          @Override
          public CharSequence getFailureHandler(Component component) {
            return "console.log(\"updateLabelAjaxAttributes::I\'m executed on failed Ajax call\")";
          }
          @Override
          public CharSequence getCompleteHandler(Component component) {
            return "console.log(\"updateLabelAjaxAttributes::I\'m executed on completed Ajax call\")";
          }
        };
        attributes.getAjaxCallListeners().add(myAjaxCallListener);
			}
			
			@Override
			protected void updateEditorAjaxAttributes(AjaxRequestAttributes attributes) {
				//when switching from label to editor
				Map<String,Object> extraParameters = attributes.getExtraParameters();
				extraParameters.put("assignmentId", assignmentId);
				extraParameters.put("studentUuid", studentGrades.getStudentUuid());


        AjaxCallListener myAjaxCallListener = new AjaxCallListener() {
          @Override
          public CharSequence getBeforeSendHandler(Component component) {
            return "console.log(\"updateEditorAjaxAttributes::I\'m executed before the firing of the Ajax call\")";
          }

          @Override
          public CharSequence getSuccessHandler(Component component) {
            return "console.log(\"updateEditorAjaxAttributes::I\'m executed on successful Ajax call\")";
          }

          @Override
          public CharSequence getFailureHandler(Component component) {
            return "console.log(\"updateEditorAjaxAttributes::I\'m executed on failed Ajax call\")";
          }
          @Override
          public CharSequence getCompleteHandler(Component component) {
            return "console.log(\"updateEditorAjaxAttributes::I\'m executed on completed Ajax call\")";
          }
        };
        attributes.getAjaxCallListeners().add(myAjaxCallListener);
			}
		};
		
		grade.setType(String.class);
		
		
		add(grade);
		
		//TODO since we are using a custom column panel here we still need this to be editable
		
		//menu
		

	}
	
	/**
	 * Format a grade to remove the .0 if present.
	 * @param grade
	 * @return
	 */
	private String formatGrade(String grade) {
		return StringUtils.removeEnd(grade, ".0");		
	}

}
