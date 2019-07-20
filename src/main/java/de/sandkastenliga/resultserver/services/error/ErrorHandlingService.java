package de.sandkastenliga.resultserver.services.error;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ErrorHandlingService {

    public void handleError(final Throwable t, final String... errCode) {
        // TODO: handle error correctly
        t.printStackTrace();
    }

}
