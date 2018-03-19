package com.blueobject.app.alive.helper;
import android.content.Context;
import org.bitbucket.stefanodp91.model.AbstractWizardModel;
import org.bitbucket.stefanodp91.model.BasicPage;
import org.bitbucket.stefanodp91.model.BranchPage;
import org.bitbucket.stefanodp91.model.ContactPage;
import org.bitbucket.stefanodp91.model.DatePage;
import org.bitbucket.stefanodp91.model.DaysPickerPage;
import org.bitbucket.stefanodp91.model.EmergencyPage;
import org.bitbucket.stefanodp91.model.GuardsPage;
import org.bitbucket.stefanodp91.model.HealthPage;
import org.bitbucket.stefanodp91.model.ImagePage;
import org.bitbucket.stefanodp91.model.MultipleFixedChoicePage;
import org.bitbucket.stefanodp91.model.NotePage;
import org.bitbucket.stefanodp91.model.NumberPage;
import org.bitbucket.stefanodp91.model.PageList;
import org.bitbucket.stefanodp91.model.PasswordPage;
import org.bitbucket.stefanodp91.model.SingleFixedChoicePage;
import org.bitbucket.stefanodp91.model.TitlePage;

public class WizardModel extends AbstractWizardModel {
    public WizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return  new PageList(
                    new BasicPage(this),
                    new ContactPage(this),
                    new HealthPage(this),
                    new EmergencyPage(this),
                    new GuardsPage(this));
    }
}