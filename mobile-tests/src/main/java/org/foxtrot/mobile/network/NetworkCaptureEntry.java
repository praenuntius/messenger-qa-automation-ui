package org.foxtrot.mobile.network;

/**
 * Снимок HTTP(S) exchange через MITM.
 */
public record NetworkCaptureEntry(String method, String url, String requestBody, int statusCode, String responseBody,
                                  boolean mocked) {
}
