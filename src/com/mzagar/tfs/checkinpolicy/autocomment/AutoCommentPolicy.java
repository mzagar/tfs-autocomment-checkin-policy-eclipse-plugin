package com.mzagar.tfs.checkinpolicy.autocomment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.microsoft.tfs.core.checkinpolicies.PolicyBase;
import com.microsoft.tfs.core.checkinpolicies.PolicyContext;
import com.microsoft.tfs.core.checkinpolicies.PolicyContextKeys;
import com.microsoft.tfs.core.checkinpolicies.PolicyEditArgs;
import com.microsoft.tfs.core.checkinpolicies.PolicyEvaluationCancelledException;
import com.microsoft.tfs.core.checkinpolicies.PolicyFailure;
import com.microsoft.tfs.core.checkinpolicies.PolicyType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.WorkItemCheckinInfo;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.memento.Memento;
import com.microsoft.tfs.core.pendingcheckin.PendingCheckin;

/**
 * <p>
 * Implements the auto comment TFS check-in policy.
 * </p>
 * If work item is assigned to pending check-in, no comment is required and
 * comment will be auto generated from work item type, id and name in format
 * 'type#id, title'.
 * </p>
 */
public class AutoCommentPolicy extends PolicyBase
{
    private final static PolicyType TYPE = new PolicyType(
        "com.mzagar.tfs.checkinpolicy.autocomment.AutoCommentPolicy-1",
        "Auto Comment Policy",
        "Autogenerates checkin comment from associated work item",
        "Allows for empty checkin comment only if at least one work item is associated in which case auto-comment will be generated",
        "Copy plugin JAR in eclipse dropins folder");

    public AutoCommentPolicy() {
        super();
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public boolean edit(final PolicyEditArgs policyEditArgs) {
        return false;
    }

    @Override
    public PolicyFailure[] evaluate(final PolicyContext context) throws PolicyEvaluationCancelledException {
        
        final PendingCheckin pc = getPendingCheckin();

        if (pc.getPendingChanges().getComment() != null && pc.getPendingChanges().getComment().length() > 0) {
            return new PolicyFailure[0];
        }

        StringBuilder autoCommentBuilder = new StringBuilder();

        for (WorkItemCheckinInfo wiCheckinInfo : pc.getWorkItems().getCheckedWorkItems()) {
            WorkItem wi = wiCheckinInfo.getWorkItem();
            String title = wi.getTitle();
            int id = wi.getID();
            autoCommentBuilder.append(String.format("%s#%s, %s\n", wi.getType().getName(), id, title));
        }

        String autoComment = autoCommentBuilder.toString();

        if (autoComment.isEmpty()) {
            return new PolicyFailure[] {
                         new PolicyFailure("Comment is mandatory if no work item is associated.", this)};
        }
        
        pc.getPendingChanges().setComment(autoComment);

        return new PolicyFailure[0];
    }

    @Override
    public PolicyType getPolicyType() {
        return AutoCommentPolicy.TYPE;
    }

    @Override
    public void displayHelp(final PolicyFailure failure, final PolicyContext context) {
        final Shell shell = (Shell) context.getProperty(PolicyContextKeys.SWT_SHELL);
        if (shell == null) {
            return;
        }

        final MessageBox helpMessageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
        helpMessageBox.setText("AutoCommentPolicy Help");
        helpMessageBox.setMessage(
                "If at least one work item is associated check-in comment will be auto-generated" +
        		"from work item type, id and title. Otherwise, comment is required.");
        helpMessageBox.open();
    }

    @Override
    public void activate(final PolicyFailure failure, final PolicyContext context) {
        final Shell shell = (Shell) context.getProperty(PolicyContextKeys.SWT_SHELL);
        if (shell == null) {
            return;
        }

        final MessageBox helpMessageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
        helpMessageBox.setText("AutoComment Policy");
        helpMessageBox.setMessage("Either associate a workitem to autogenerate the comment on checkin or type the comment.");
        helpMessageBox.open();
    }

    @Override
    public void loadConfiguration(final Memento configurationMemento) {
    }

    @Override
    public void saveConfiguration(final Memento configurationMemento) {
    }
}
