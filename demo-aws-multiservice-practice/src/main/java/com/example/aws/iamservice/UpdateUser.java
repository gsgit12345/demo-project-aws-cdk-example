package com.example.aws.iamservice;


import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.UpdateUserRequest;
import com.amazonaws.services.identitymanagement.model.UpdateUserResult;



public class UpdateUser {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply the current username and a new\n" +
                "username. Ex:\n\n" +
                "UpdateUser <current-name> <new-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String cur_name = args[0];
        String new_name = args[1];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        UpdateUserRequest request = new UpdateUserRequest()
                .withUserName(cur_name)
                .withNewUserName(new_name);

        UpdateUserResult response = iam.updateUser(request);

        System.out.printf("Successfully updated user to username %s",
                new_name);
    }
}
