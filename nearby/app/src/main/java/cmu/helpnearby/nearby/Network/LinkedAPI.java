package cmu.helpnearby.nearby.Network;

/**
 * Created by evan on 7/26/15.
 */
public interface LinkedAPI {
    String JSON_ID = "id";
    String JSON_NAME = "formattedName";
    String JSON_EMAIL = "emailAddress";
    String JSON_HEADLINE ="headline";
    String JSON_PICTUREURL = "pictureUrl";

    String host = "api.linkedin.com";
    String basicUserInfoUrl = "https://" + host + "/v1/people/~:(id,formatted-name,email-address,headline,picture-url)";
    String shareUrl = "https://" + host + "/v1/people/~/shares";
}
