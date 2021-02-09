import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContinuousIntegrationServerTests {
    @Test
	@DisplayName("Check for build success")
	void checkBuildSuccessForRepo() {
        String testRequestBodyJson = "{\"head_commit\": {\"id\": \"7bb8b28ba9d8ae15c063f784ba72ed606a3d344a\"}, \"repository\": {\"clone_url\": \"https://github.com/MichaelaSahlgren/DD2480.git\"}}";
        String result = ContinuousIntegrationServer.processWebhookCommit(testRequestBodyJson);
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
  @DisplayName("Sends sucess-email when build successful")
  void sendEmailCorrectWhenBuildSuccess() {
      /*TODO*/
  }
  @Test
  @DisplayName("Sends failure-email when build successful")
  void sendEmailCorrectWhenBuildFails() {
      /*TODO*/
  }
}
