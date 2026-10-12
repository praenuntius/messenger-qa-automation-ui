package org.foxtrot.mobile.network;

public record Mock(String urlContains, int statusCode, String body) {
}
