package MyApp.BE.service.ZoneTime;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class ZoneTimeService implements IZoneTimeService {
    @Override
    public OffsetDateTime setZoneTime(String timeZoneArea) {

        return ZonedDateTime.now(ZoneId.of(timeZoneArea)).toOffsetDateTime();
    }
}
