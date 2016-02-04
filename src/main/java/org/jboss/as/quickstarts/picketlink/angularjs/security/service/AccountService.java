package org.jboss.as.quickstarts.picketlink.angularjs.security.service;

import org.jboss.as.quickstarts.picketlink.angularjs.model.Person;
import org.jboss.as.quickstarts.picketlink.angularjs.security.model.ApplicationRole;
import org.jboss.as.quickstarts.picketlink.angularjs.security.model.IdentityModelManager;
import org.jboss.as.quickstarts.picketlink.angularjs.security.model.MyUser;
import org.jboss.as.quickstarts.picketlink.angularjs.security.service.datatypes.dtAccount;
import org.jboss.as.quickstarts.picketlink.angularjs.util.MessageBuilder;
import org.picketlink.Identity;
import org.picketlink.authorization.annotations.RolesAllowed;
import org.picketlink.idm.model.Account;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path("/private/account")
@RolesAllowed(ApplicationRole.ADMINISTRATOR)
public class AccountService {

    @Inject
    private Identity identity;

    @Inject
    private IdentityModelManager identityModelManager;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public List<dtAccount> getAccount() {
        Person person = ((MyUser) identity.getAccount()).getPerson();
        dtAccount accInfo = new dtAccount();

        accInfo.setFirstName(person.getFirstName());
        accInfo.setLastName(person.getLastName());
        accInfo.setEmail(person.getEmail());

        List<dtAccount> accInfoList = new ArrayList<>();
        accInfoList.add(accInfo);
        return accInfoList;
    }
    
    @POST
    @Path("enableAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response enable(Person passedUser) {
        MessageBuilder message;

        MyUser user = this.identityModelManager.findByLoginName(passedUser.getEmail());

        if (user == null) {
            return MessageBuilder.badRequest().message("Invalid account.").build();
        }

        if(user.isEnabled()) {
            return MessageBuilder.badRequest().message("Account is already enabled.").build();
        }

        this.identityModelManager.enableAccount(user);

        return MessageBuilder.ok().message("Account is now enabled.").build();
    }

    @POST
    @Path("disableAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response disable(Person passedUser) {
        MessageBuilder message;

        MyUser user = this.identityModelManager.findByLoginName(passedUser.getEmail());

        if (user == null) {
            return MessageBuilder.badRequest().message("Invalid account.").build();
        }

        if(!user.isEnabled()) {
            return MessageBuilder.badRequest().message("Accound is already disabled.").build();
        }

        this.identityModelManager.disableAccount(user);

        return MessageBuilder.ok().message("Account is now disabled.").build();
    }
}
