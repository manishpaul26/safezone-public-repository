package com.track.safezone.services;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class FaceClientService {

    private static final String apiEndpoint = "https://safezone-app.cognitiveservices.azure.com/face/v1.0";
    // Add your Face subscription key to your environment variables.
    private static final String subscriptionKey = "d42f464dbac14bbd82e5ea6f85ad07d3";

    private static final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private FaceClientService() {

    }

    public static FaceServiceClient getFaceServiceClient() {
        return faceServiceClient;
    }
}
