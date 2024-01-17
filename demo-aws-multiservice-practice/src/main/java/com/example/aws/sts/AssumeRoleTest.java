package com.example.aws.sts;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;

public class AssumeRoleTest {
    public static void main(String str[])
    {
        String roleArn="arn:aws:iam::382974126643:role/assumethisrole"; //assumerole arn
        String roleSessionName="session_1";
        AWSSecurityTokenService stsClient= AWSSecurityTokenServiceClientBuilder.standard().build();
        AssumeRoleRequest roleRequest=new com.amazonaws.services.securitytoken.model.AssumeRoleRequest().
                withRoleArn(roleArn).withRoleSessionName(roleSessionName).withDurationSeconds(36000);

        AssumeRoleResult assumeRoleResult=stsClient.assumeRole(roleRequest);
        Credentials teCredentials=assumeRoleResult.getCredentials();
        System.out.println("AccessKeyId"+teCredentials.getSecretAccessKey());
        System.out.println("SecretKeyId"+teCredentials.getSessionToken());

        System.out.println("session_token"+teCredentials.getAccessKeyId());

        AWSCredentials awsCredentials=new BasicSessionCredentials(teCredentials.getAccessKeyId(),teCredentials.getSecretAccessKey(),teCredentials.getAccessKeyId());

        AWSCredentialsProvider  provider=new AWSStaticCredentialsProvider(awsCredentials);

        AmazonIdentityManagement client= AmazonIdentityManagementClientBuilder.standard().withCredentials(provider).build();

        client.listRoles().getRoles().forEach(r->System.out.println(r.getArn()));


    }
    }

