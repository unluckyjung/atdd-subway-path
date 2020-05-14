package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import wooteco.subway.admin.dto.PathResponse;
import wooteco.subway.admin.dto.PathResponses;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class PathSearchAcceptanceTest extends AcceptanceTest {
    @DisplayName("경로 조회 서비스를 제공한다.")
    @Test
    void searchPath() {
        // Given 여러 노선이 추가 되어 있다.
        createLine(LINE_NAME_SINBUNDANG);
        createLine(LINE_NAME_BUNDANG);
        createLine(LINE_NAME_2);
        createLine(LINE_NAME_3);
        // And 여러 지하철 역 추가 되어 있다.
        createStation(STATION_NAME_KANGNAM);
        createStation(STATION_NAME_YEOKSAM);
        createStation(STATION_NAME_SEOLLEUNG);
        // And 여러 지하철 역이 노선에 추가 되어 있다.
        addLineStation(3L, null, 1L);
        addLineStation(3L, 1L, 2L);
        addLineStation(3L, 2L, 3L);

        // When  경로에 필요한 출발역과 도착역을 입력한 후, 조회 요청을 보낸다.
        // Then  경로를 응답 받는다.
        PathResponses response = getPath(STATION_NAME_KANGNAM, STATION_NAME_SEOLLEUNG);
        Map<String, PathResponse> paths = response.getResponse();
        paths.forEach((edgeWeight, pathResponse) -> {
            assertThat(pathResponse.getStationResponses().size()).isEqualTo(3);
            // And   소요시간, 경로 길이, 역 목록을 응답 받는다.
            assertThat(pathResponse.getTotalDistance()).isEqualTo(20);
            assertThat(pathResponse.getTotalDuration()).isEqualTo(20);
        });

    }

    private PathResponses getPath(String departure, String arrival) {
        return given().when()
            .get("/lines/path/" + departure + "/" + arrival)
            .then()
            .log().all()
            .extract().as(PathResponses.class);
    }
}
