package com.selfcoders.matterbukkit.matterbridgeapi;

import java.io.IOException;

class APIException extends IOException {
    APIException(String message) {
        super(message);
    }
}
