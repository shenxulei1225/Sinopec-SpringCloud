package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ActionGroupMemberParserTest {

    private final ActionGroupMemberParser parser = new ActionGroupMemberParser(new ObjectMapper());

    @Test
    void parse_keepsDuplicateActionsWithIndependentMemberParameters() {
        List<ActionGroupMemberParser.Member> members = parser.parse(Map.of(
                "version", 1,
                "members", List.of(
                        Map.of(
                                "memberKey", "photo-near",
                                "actionCode", "photo",
                                "order", 1,
                                "params", Map.of("distance", 2)),
                        Map.of(
                                "memberKey", "photo-far",
                                "actionCode", "photo",
                                "order", 0,
                                "params", Map.of("distance", 5),
                                "required", false)
                )
        ));

        assertEquals(List.of("photo-far", "photo-near"),
                members.stream().map(ActionGroupMemberParser.Member::memberKey).toList());
        assertEquals(5, members.get(0).params().get("distance"));
        assertFalse(members.get(0).required());
    }
}
