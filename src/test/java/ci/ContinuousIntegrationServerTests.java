package ci;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


import org.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.util.stream.Collectors;
import org.json.*;
import java.nio.file.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class ContinuousIntegrationServerTests {
    Request baseRequest;
    HttpServletRequest request;
    HttpServletResponse response;
    JSONObject requestBodyJson;

    /**
     * Unit tests for CI server:
     * "known" commits are tested against the methods in ContinuousIntegrationServer class to ensure that they work as they should. 
     * If a method stops working as it should, these test classes will fail resulting in build fauilure for CI-server.
    */
    @Test
  	@DisplayName("Check for build success")
  	void checkBuildSuccessForRepo() {
          requestBodyJson = new JSONObject("{\"head_commit\": {\"id\": \"7bb8b28ba9d8ae15c063f784ba72ed606a3d344a\"}, \"repository\": {\"clone_url\": \"https://github.com/MichaelaSahlgren/DD2480.git\"},\"author\":{\"name\":\"Name\",\"email\":\"test@mail.com\",\"username\":\"UserName\"}}");
          String result = ContinuousIntegrationServer.processWebhookCommit(requestBodyJson);
  		assertTrue(result.contains("BUILD SUCCESS"));
  	}

    @Test
    @DisplayName("Clone example repo and run tests")
  	void cloneAndTestRepoTest() {
          String repoUrl = "https://github.com/MichaelaSahlgren/DD2480.git";
          String commitId = "7bb8b28ba9d8ae15c063f784ba72ed606a3d344a";
          String result = ContinuousIntegrationServer.cloneAndTest(repoUrl, commitId);
  		assertTrue(result.contains("BUILD SUCCESS"));
  	}


    
    @Test
    @DisplayName("Check for build failure when compilation error")
  	void checkBuildFauilureForCompilationFailure() {
          String repoUrl = "https://github.com/George-Bassilious/CI-DD2480.git";
          String commitId = "8ce7f48137f49de6591acc92dd0c7480c27180de";
          String result = ContinuousIntegrationServer.cloneAndTest(repoUrl, commitId);
      //for this test to pass both of these statements must be in the commit message
      //this ensures that the build failed because of compilation errors.
      assertAll(
      ()->assertTrue(result.contains("Compilation failure")),
      ()->assertTrue(result.contains("BUILD FAILURE"))
      );
  	
  	}

    @Test
    @DisplayName("Check for build failure when junit test fails")
  	void checkBuildFauilureForUnitTestFailure() {
          String repoUrl = "https://github.com/George-Bassilious/CI-DD2480.git";
          String commitId = "6d670e4a1387d9ce3e28643a74bf5113d19007ba";
          String result = ContinuousIntegrationServer.cloneAndTest(repoUrl, commitId);

      //for this test to pass both of these statements must be in the commit message
      //this ensures that the build failed because of assertion errors in the test cases.
      assertAll(
      ()-> assertTrue(result.contains(".AssertionFailedError")),
      ()-> assertTrue(result.contains("BUILD FAILURE"))
      );  
  		
  	}





    //Unit Tests for methods sending email notification
    @Test
    //If build succeeds
    void sendEmailTestingSuccess(){
      requestBodyJson = new JSONObject("{\"head_commit\": {\"id\": \"7bb8b28ba9d8ae15c063f784ba72ed606a3d344a\", \"committer\":{\"name\":\"Name\",\"email\":\"sara.damne@gmail.com\",\"username\":\"UserName\"}}, \"repository\": {\"clone_url\": \"https://github.com/MichaelaSahlgren/DD2480.git\"}}");

      assertTrue(ContinuousIntegrationServer.sendGmail(requestBodyJson, "BUILD SUCCESS"));
    }
    @Test
    //If build fails
    void sendEmailTestingFailure(){
      requestBodyJson = new JSONObject("{\"head_commit\": {\"id\": \"7bb8b28ba9d8ae15c063f784ba72ed606a3d344a\", \"committer\":{\"name\":\"Name\",\"email\":\"sara.damne@gmail.com\",\"username\":\"UserName\"}}, \"repository\": {\"clone_url\": \"https://github.com/MichaelaSahlgren/DD2480.git\"}}");

      assertTrue(ContinuousIntegrationServer.sendGmail(requestBodyJson, "BUILD FAILURE"));
    }

}
