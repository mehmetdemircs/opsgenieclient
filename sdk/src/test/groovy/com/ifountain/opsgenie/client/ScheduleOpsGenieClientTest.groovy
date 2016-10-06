package com.ifountain.opsgenie.client

import com.ifountain.opsgenie.client.http.HttpTestRequest
import com.ifountain.opsgenie.client.http.HttpTestRequestListener
import com.ifountain.opsgenie.client.http.HttpTestResponse
import com.ifountain.opsgenie.client.model.beans.*
import com.ifountain.opsgenie.client.model.schedule.*
import com.ifountain.opsgenie.client.test.util.OpsGenieClientTestCase
import com.ifountain.opsgenie.client.util.JsonUtils
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.junit.Test

import java.text.SimpleDateFormat

import static org.junit.Assert.*

class ScheduleOpsGenieClientTest extends OpsGenieClientTestCase implements HttpTestRequestListener {
    @Test
    public void testAddScheduleSuccessfully() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"id\":\"schedule1Id\", \"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        AddScheduleRequest request = new AddScheduleRequest();
        request.setApiKey("customer1");
        request.setName("schedule1");
        request.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        request.setEnabled(true);
        request.setRotations([
                new ScheduleRotation(name:"rule1", startDate: new Date(10000000000l), rotationType: ScheduleRotation.RotationType.hourly, rotationLength: 8,
                        restrictions: [
                                new ScheduleRotationRestriction(startDay: ScheduleRotationRestriction.DAY.monday, startHour: 0, startMin: 0, endDay: ScheduleRotationRestriction.DAY.sunday, endHour: 23, endMin: 30)
                        ],
                        participants: [
                                new ScheduleParticipant(participant: "group1", type: ScheduleParticipant.Type.group),
                                new ScheduleParticipant(participant: "escalation1", type: ScheduleParticipant.Type.escalation),
                                new ScheduleParticipant(participant: "team1", type: ScheduleParticipant.Type.team)
                        ]
                ),
                new ScheduleRotation(startDate: new Date(20000000000l), rotationType: ScheduleRotation.RotationType.daily,
                        participants: [
                                new ScheduleParticipant(participant: "user1@xyz.com", type: ScheduleParticipant.Type.user),
                        ])
        ]);

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().addSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpPost.METHOD_NAME, requestSent.getMethod());
        assertEquals("/v1/json/schedule", requestSent.getUrl())
        assertEquals("application/json; charset=utf-8", requestSent.getHeader(HttpHeaders.CONTENT_TYPE));

        def jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertEquals(request.getName(), jsonContent[TestConstants.API.NAME])
        assertEquals(request.getTimeZone().getID(), jsonContent[TestConstants.API.TIMEZONE])
        assertTrue(jsonContent[TestConstants.API.ENABLED])

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(request.getTimeZone())
        assertEquals(2, jsonContent[TestConstants.API.ROTATIONS].size())

        //first rule
        def ruleObject = request.getRotations()[0];
        def rule = jsonContent[TestConstants.API.ROTATIONS].find { it.startDate == sdf.format(ruleObject.getStartDate()) }
        assertEquals("rule1",rule[TestConstants.API.NAME])
        assertEquals(ruleObject.rotationType.name(), rule[TestConstants.API.ROTATION_TYPE])
        assertEquals(ruleObject.rotationLength, rule[TestConstants.API.ROTATION_LENGTH])

        assertEquals(1, rule[TestConstants.API.RESTRICTIONS].size())
        def restriction = rule[TestConstants.API.RESTRICTIONS][0]
        def restrictionObject = ruleObject.restrictions[0];
        assertEquals(restrictionObject.startDay.name(), restriction[TestConstants.API.START_DAY])
        assertEquals(restrictionObject.endDay.name(), restriction[TestConstants.API.END_DAY])
        assertEquals("00:00", restriction[TestConstants.API.START_TIME])
        assertEquals("23:30", restriction[TestConstants.API.END_TIME])

        assertEquals(3, rule[TestConstants.API.PARTICIPANTS].size())
        def participantObject = ruleObject.participants[0];
        assertNotNull(rule[TestConstants.API.PARTICIPANTS].find { it == participantObject.getParticipant() })
        participantObject = ruleObject.participants[1];
        assertNotNull(rule[TestConstants.API.PARTICIPANTS].find { it == participantObject.getParticipant() })
        participantObject = ruleObject.participants[2];
        assertNotNull(rule[TestConstants.API.PARTICIPANTS].find { it == participantObject.getParticipant() })

        //second rule
        ruleObject = request.getRotations()[1];
        rule = jsonContent[TestConstants.API.ROTATIONS].find { it.startDate == sdf.format(ruleObject.getStartDate()) }
        assertNull(rule[TestConstants.API.NAME])
        assertEquals(ruleObject.rotationType.name(), rule[TestConstants.API.ROTATION_TYPE])
        assertEquals(ruleObject.rotationLength, rule[TestConstants.API.ROTATION_LENGTH])

        assertNull(rule[TestConstants.API.RESTRICTIONS])

        assertEquals(1, rule[TestConstants.API.PARTICIPANTS].size())
        participantObject = ruleObject.participants[0];
        assertNotNull(rule[TestConstants.API.PARTICIPANTS].find { it == participantObject.getParticipant() })
    }

    @Test
    public void testAddScheduleThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "addSchedule", new AddScheduleRequest())
    }

    @Test
    public void testUpdateScheduleSuccessfully() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"id\":\"schedule1Id\", \"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        UpdateScheduleRequest request = new UpdateScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");
        request.setName("schedule1");
        request.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        request.setEnabled(false);
        request.setRotations([
                new ScheduleRotation(name:"updatedRule",startDate: new Date(20000000000l), rotationType: ScheduleRotation.RotationType.daily,
                        participants: [
                                new ScheduleParticipant(participant: "user1@xyz.com", type: ScheduleParticipant.Type.user),
                        ])
        ]);

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().updateSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpPost.METHOD_NAME, requestSent.getMethod());
        assertEquals("/v1/json/schedule", requestSent.getUrl())
        assertEquals("application/json; charset=utf-8", requestSent.getHeader(HttpHeaders.CONTENT_TYPE));

        def jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(request.getId(), jsonContent[TestConstants.API.ID])
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertEquals(request.getName(), jsonContent[TestConstants.API.NAME])
        assertEquals(request.getTimeZone().getID(), jsonContent[TestConstants.API.TIMEZONE])
        assertFalse(jsonContent[TestConstants.API.ENABLED])

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(request.getTimeZone())
        assertEquals(1, jsonContent[TestConstants.API.ROTATIONS].size())

        //first rule
        def ruleObject = request.getRotations()[0];
        def rule = jsonContent[TestConstants.API.ROTATIONS].find { it.startDate == sdf.format(ruleObject.getStartDate()) }
        assertEquals(ruleObject.name, rule[TestConstants.API.NAME])
        assertEquals(ruleObject.rotationType.name(), rule[TestConstants.API.ROTATION_TYPE])
        assertEquals(ruleObject.rotationLength, rule[TestConstants.API.ROTATION_LENGTH])

        assertNull(rule[TestConstants.API.RESTRICTIONS])

        assertEquals(1, rule[TestConstants.API.PARTICIPANTS].size())
        def participantObject = ruleObject.participants[0];
        assertNotNull(rule[TestConstants.API.PARTICIPANTS].find { it == participantObject.getParticipant() })
    }

    @Test
    public void testUpdateScheduleWithPartialUpdate() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"id\":\"schedule1Id\", \"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        UpdateScheduleRequest request = new UpdateScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().updateSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]

        def jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(2, jsonContent.size())
        assertEquals(request.getId(), jsonContent[TestConstants.API.ID])
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])

        //test update name only
        request = new UpdateScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");
        request.setName("schedule1Updated");

        response = OpsGenieClientTestCase.opsgenieClient.schedule().updateSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(2, receivedRequests.size());
        requestSent = receivedRequests[1]

        jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(3, jsonContent.size())
        assertEquals(request.getId(), jsonContent[TestConstants.API.ID])
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertEquals(request.getName(), jsonContent[TestConstants.API.NAME])

        //test update enabled only
        request = new UpdateScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");
        request.setEnabled(false)

        response = OpsGenieClientTestCase.opsgenieClient.schedule().updateSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(3, receivedRequests.size());
        requestSent = receivedRequests[2]

        jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(3, jsonContent.size())
        assertEquals(request.getId(), jsonContent[TestConstants.API.ID])
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertFalse(jsonContent[TestConstants.API.ENABLED])

        //test update timezone only
        request = new UpdateScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");
        request.setTimeZone(TimeZone.getTimeZone("GMT-7"))

        response = OpsGenieClientTestCase.opsgenieClient.schedule().updateSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(4, receivedRequests.size());
        requestSent = receivedRequests[3]

        jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(3, jsonContent.size())
        assertEquals(request.getId(), jsonContent[TestConstants.API.ID])
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertEquals(request.getTimeZone().getID(), jsonContent[TestConstants.API.TIMEZONE])

        //test update rotations only
        request = new UpdateScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");
        request.setRotations([
                new ScheduleRotation(startDate: new Date(20000000000l), rotationType: ScheduleRotation.RotationType.daily,
                        participants: [
                                new ScheduleParticipant(participant: "user1@xyz.com", type: ScheduleParticipant.Type.user),
                        ])
        ])

        response = OpsGenieClientTestCase.opsgenieClient.schedule().updateSchedule(request)
        assertEquals("schedule1Id", response.getId())
        assertEquals(1, response.getTook())

        assertEquals(5, receivedRequests.size());
        requestSent = receivedRequests[4]

        jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(3, jsonContent.size())
        assertEquals(request.getId(), jsonContent[TestConstants.API.ID])
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        assertEquals(1, jsonContent[TestConstants.API.ROTATIONS].size())
        //first rule
        def ruleObject = request.getRotations()[0];
        def rule = jsonContent[TestConstants.API.ROTATIONS].find { it.startDate == sdf.format(ruleObject.getStartDate()) }
        assertEquals(ruleObject.rotationType.name(), rule[TestConstants.API.ROTATION_TYPE])
        assertEquals(ruleObject.rotationLength, rule[TestConstants.API.ROTATION_LENGTH])

        assertNull(rule[TestConstants.API.RESTRICTIONS])

        assertEquals(1, rule[TestConstants.API.PARTICIPANTS].size())
        def participantObject = ruleObject.participants[0];
        assertNotNull(rule[TestConstants.API.PARTICIPANTS].find { it == participantObject.getParticipant() });
    }

    @Test
    public void testUpdateScheduleThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "updateSchedule", new UpdateScheduleRequest())
    }

    @Test
    public void testDeleteScheduleSuccessfully() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        DeleteScheduleRequest request = new DeleteScheduleRequest();
        request.setApiKey("customer1");
        request.setId("schedule1Id");
        request.setName("schedule1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().deleteSchedule(request)
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpDelete.METHOD_NAME, requestSent.getMethod());
        assertEquals("/v1/json/schedule", requestSent.getUrl())

        assertEquals(3, requestSent.getParameters().size())
        assertEquals(request.getId(), requestSent.getParameters()[TestConstants.API.ID]);
        assertEquals(request.getName(), requestSent.getParameters()[TestConstants.API.NAME]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
    }


    @Test
    public void testDeleteUserThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "deleteSchedule", new DeleteScheduleRequest())
    }

    @Test
    public void testGetScheduleSuccessfully() throws Exception {
        Map jsonContent = new HashMap();
        jsonContent.put("took", 1);
        jsonContent.put(TestConstants.API.ID, "schedule1id");
        jsonContent.put(TestConstants.API.NAME, "schedule1");
        jsonContent.put(TestConstants.API.TEAM, "team1");
        jsonContent.put(TestConstants.API.TIMEZONE, "GMT+2");
        jsonContent.put(TestConstants.API.ENABLED, false);
        jsonContent.put(TestConstants.API.RULES, [
                [
                        id: "r1",
                        name: "rule1",
                        startDate   : "2013-01-24 22:00", rotationType: "daily", rotationLength: 7,
                        participants: [
                                [participant: "group1", type: "group"],
                                [participant: "user1@xyz.com", type: "user"],
                                [participant: "escalation1", type: "escalation"]
                        ],
                        restrictions: [
                                [startDay: "monday", startTime: "00:00", endDay: "sunday", endTime: "22:30"],
                                [startDay: "tuesday", startTime: "02:00", endDay: "wednesday", endTime: "12:30"]
                        ]
                ],
                [
                        id: "r2",
                        startDate   : "2013-02-25 22:00", rotationType: "weekly",
                        participants: [
                                [participant: "group3", type: "group"]
                        ],
                ]
        ]);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson(jsonContent).getBytes(), 200, "application/json; charset=utf-8"))

        GetScheduleRequest request = new GetScheduleRequest();
        request.setId(jsonContent[TestConstants.API.ID]);
        request.setName(jsonContent[TestConstants.API.NAME]);
        request.setApiKey("customer1");

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(jsonContent[TestConstants.API.TIMEZONE]))

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().getSchedule(request)
        assertEquals(1, response.getTook())
        assertEquals(jsonContent[TestConstants.API.NAME], response.getSchedule().name)
        assertEquals(jsonContent[TestConstants.API.TEAM], response.getSchedule().team)
        assertEquals(jsonContent[TestConstants.API.ID], response.getSchedule().id)
        assertEquals(sdf.getTimeZone().getID(), response.getSchedule().getTimeZone().getID())
        assertEquals(jsonContent[TestConstants.API.ENABLED], response.getSchedule().isEnabled())
        assertEquals(2, response.getSchedule().rotations.size())

        //check first rule
        def rule = response.getSchedule().rotations.find { !it.restrictions.isEmpty() }
        def ruleMap = jsonContent[TestConstants.API.RULES][0]

        assertEquals("rule1",rule.name)
        assertEquals("r1",rule.id)
        assertEquals(ScheduleRotation.RotationType.daily, rule.rotationType)
        assertEquals(7, rule.rotationLength)
        assertEquals(sdf.parse(ruleMap[TestConstants.API.START_DATE]), rule.startDate)
        assertEquals(3, rule.getParticipants().size())


        int index = 0;
        ruleMap[TestConstants.API.PARTICIPANTS].each { participantMap ->
            assertEquals(participantMap.participant, rule.getParticipants()[index].getParticipant())
            assertEquals(participantMap.type, rule.getParticipants()[index].getType().name())
            index++;
        }

        //check second rule
        rule = response.getSchedule().rotations.find { it.restrictions == null }
        ruleMap = jsonContent[TestConstants.API.RULES][1]

        assertEquals("r2",rule.id)

        assertEquals(ScheduleRotation.RotationType.weekly, rule.rotationType)
        assertEquals(sdf.parse(ruleMap[TestConstants.API.START_DATE]), rule.startDate)
        assertEquals(1, rule.getParticipants().size())

        assertEquals(ruleMap[TestConstants.API.PARTICIPANTS][0].participant, rule.getParticipants()[0].getParticipant())
        assertEquals(ruleMap[TestConstants.API.PARTICIPANTS][0].type, rule.getParticipants()[0].getType().name())


        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getId(), requestSent.getParameters()[TestConstants.API.ID]);
        assertEquals(request.getName(), requestSent.getParameters()[TestConstants.API.NAME]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals("/v1/json/schedule", requestSent.getUrl())
    }

    @Test
    public void testGetUserThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "getSchedule", new GetScheduleRequest(id: "sch1"))
    }

    @Test
    public void testWhoIsOnCallSuccessfully() throws Exception {
        Map jsonContent = new HashMap();
        jsonContent.put("took", 1);
        jsonContent.put(TestConstants.API.ID, "id_schedule1");
        jsonContent.put(TestConstants.API.NAME, "schedule1");
        jsonContent.put(TestConstants.API.TYPE, "schedule");
        jsonContent.put(TestConstants.API.PARTICIPANTS, [
                [name: "group1", type: "group", id: "id_group1", participants: [
                        [name: "user3@xyz.com", type: "user", forwarded: false, id: "id_user3"],
                        [name: "user4@xyz.com", type: "user", forwarded: false, id: "id_user4"]
                ]],
                [name: "user1@xyz.com", type: "user", forwarded: false, id: "id_user1"],
                [name: "user2@xyz.com", type: "user", forwarded: true, id: "id_user2", forwardedFrom: [
                        id  : "id_user7",
                        name: "user7@xyz.com",
                        type: "user"
                ]],
                [name: "escalation1", type: "escalation", id: "esc1", participants: [
                        [name: "user5@xyz.com", type: "user", forwarded: false, id: "id_user5", escalationTime: 2, notifyType: "default"],
                        [name: "group2", type: "group", id: "id_group2", escalationTime: 4, notifyType: "default", participants: [
                                [name: "user6@xyz.com", type: "user", forwarded: false, id: "id_user6"]
                        ]],
                        [name: "schedule2", type: "schedule", id: "sched2", escalationTime: 54, notifyType: "next"]
                ]],
                [name:"tim", type: "team", id: "tim1"]
        ]);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson(jsonContent).getBytes(), 200, "application/json; charset=utf-8"))

        WhoIsOnCallRequest request = new WhoIsOnCallRequest();
        request.setId(jsonContent[TestConstants.API.ID]);
        request.setName(jsonContent[TestConstants.API.NAME]);
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().whoIsOnCall(request)
        assertEquals(1, response.getTook())
        assertEquals("schedule1", response.getWhoIsOnCall().name)
        assertEquals("id_schedule1", response.getWhoIsOnCall().id)
        assertEquals("schedule", response.getWhoIsOnCall().type)
        assertEquals(5, response.getWhoIsOnCall().participants.size())

        int index = 0;
        jsonContent[TestConstants.API.PARTICIPANTS].each {
            assertEquals(it.name, response.whoIsOnCall.participants[index].name)
            assertEquals(it.type, response.whoIsOnCall.participants[index].type)
            assertEquals(it.id, response.whoIsOnCall.participants[index].id)
            if (it.type == "user") {
                assertEquals(it.forwarded, response.whoIsOnCall.participants[index].forwarded)
            }
            index++;
        }

        WhoIsOnCall forwardedUserData = response.whoIsOnCall.participants.find {
            it.name == "user2@xyz.com"
        }
        assertNotNull(forwardedUserData);

        assertEquals(true, forwardedUserData.forwarded);

        WhoIsOnCall forwardedFromData = forwardedUserData.forwardedFrom;
        assertNotNull(forwardedFromData);

        assertEquals("user7@xyz.com", forwardedFromData.name)
        assertEquals("user", forwardedFromData.type)
        assertEquals("id_user7", forwardedFromData.id)

        WhoIsOnCall groupData = response.whoIsOnCall.participants.find {
            it.name == "group1"
        };
        assertNotNull(groupData);

        assertEquals(2, groupData.participants.size())
        assertEquals("user3@xyz.com", groupData.participants[0].name)
        assertEquals("user4@xyz.com", groupData.participants[1].name)

        WhoIsOnCall escalationData = response.whoIsOnCall.participants.find {
            it.name == "escalation1"
        };
        assertNotNull(escalationData);

        assertEquals(3, escalationData.participants.size())

        assertEquals("user5@xyz.com", escalationData.participants[0].name)
        assertEquals(2, escalationData.participants[0].escalationTime)
        assertEquals("default", escalationData.participants[0].notifyType)

        WhoIsOnCall escGroupData = escalationData.participants.find { it.name == "group2" };
        assertNotNull(escGroupData);

        assertEquals("group2", escGroupData.name)
        assertEquals(4, escGroupData.escalationTime)
        assertEquals("default", escGroupData.notifyType)

        assertEquals(1, escGroupData.participants.size())
        assertEquals("user6@xyz.com", escGroupData.participants[0].name)

        def scheduleData = escalationData.participants.find { it.name == "schedule2" }
        assertNotNull(scheduleData)
        assertEquals(54, scheduleData.escalationTime)
        assertEquals("next", scheduleData.notifyType)

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getId(), requestSent.getParameters()[TestConstants.API.ID]);
        assertEquals(request.getName(), requestSent.getParameters()[TestConstants.API.NAME]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertFalse(requestSent.getParameters().containsKey(TestConstants.API.TIMEZONE))
        assertFalse(requestSent.getParameters().containsKey(TestConstants.API.TIME))
        assertEquals("/v1.1/json/schedule/whoIsOnCall", requestSent.getUrl())
    }

    @Test
    public void testFlatWhoIsOnCallSuccessfully() throws Exception {
        def recipientList = [
                "user1@xyz.com",
                "user2@xyz.com",
                "user3@xyz.com",
                "user4@xyz.com"
        ]
        Map jsonContent = new HashMap();
        jsonContent.put("took", 1);
        jsonContent.put(TestConstants.API.ID, "id_schedule1");
        jsonContent.put(TestConstants.API.NAME, "schedule1");
        jsonContent.put(TestConstants.API.TYPE, "schedule");
        jsonContent.put(TestConstants.API.RECIPIENTS, recipientList);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson(jsonContent).getBytes(), 200, "application/json; charset=utf-8"))

        FlatWhoIsOnCallRequest request = new FlatWhoIsOnCallRequest();
        request.setId(jsonContent[TestConstants.API.ID]);
        request.setName(jsonContent[TestConstants.API.NAME]);
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().flatWhoIsOnCall(request)
        assertEquals(1, response.getTook())
        assertEquals("schedule1", response.getWhoIsOnCall().name)
        assertEquals("id_schedule1", response.getWhoIsOnCall().id)
        assertEquals("schedule", response.getWhoIsOnCall().type)
        assertEquals(recipientList, response.getWhoIsOnCall().recipients)

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getId(), requestSent.getParameters()[TestConstants.API.ID]);
        assertEquals(request.getName(), requestSent.getParameters()[TestConstants.API.NAME]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertFalse(requestSent.getParameters().containsKey(TestConstants.API.TIMEZONE))
        assertFalse(requestSent.getParameters().containsKey(TestConstants.API.TIME))
        assertEquals("true", requestSent.getParameters()[TestConstants.API.FLAT])
        assertEquals("/v1.1/json/schedule/whoIsOnCall", requestSent.getUrl())
    }

    @Test
    public void testWhoIsOnCallSuccessfullyWithOptionalParams() throws Exception {
        Map jsonContent = new HashMap();
        jsonContent.put("took", 1);
        jsonContent.put(TestConstants.API.NAME, "schedule1");
        jsonContent.put(TestConstants.API.PARTICIPANTS, [
                [name: "group1", type: "group"],
                [name: "user1@xyz.com", type: "user", forwarded: false],
                [name: "user2@xyz.com", type: "user", forwarded: true],
                [name: "escalation1", type: "escalation"],
                [name: "tim", type: "team"]
        ]);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson(jsonContent).getBytes(), 200, "application/json; charset=utf-8"))

        WhoIsOnCallRequest request = new WhoIsOnCallRequest();
        request.setId(jsonContent[TestConstants.API.ID]);
        request.setName(jsonContent[TestConstants.API.NAME]);
        request.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        request.setTime(new Date(System.currentTimeMillis()));
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().whoIsOnCall(request)
        assertEquals(1, response.getTook())
        assertEquals(jsonContent[TestConstants.API.NAME], response.getWhoIsOnCall().name)
        assertEquals(5, response.getWhoIsOnCall().participants.size())

        int index = 0;
        jsonContent[TestConstants.API.PARTICIPANTS].each {
            assertEquals(it.name, response.whoIsOnCall.participants[index].name)
            assertEquals(it.type, response.whoIsOnCall.participants[index].type)
            if (it.type == "user") {
                assertEquals(it.forwarded, response.whoIsOnCall.participants[index].forwarded)
            }
            index++;
        }


        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(request.getTimeZone())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getId(), requestSent.getParameters()[TestConstants.API.ID]);
        assertEquals(request.getName(), requestSent.getParameters()[TestConstants.API.NAME]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals(request.getTimeZone().getID(), requestSent.getParameters()[TestConstants.API.TIMEZONE])
        assertEquals(sdf.format(request.getTime()), requestSent.getParameters()[TestConstants.API.TIME])
        assertEquals("false", requestSent.getParameters()[TestConstants.API.FLAT])
        assertEquals("/v1.1/json/schedule/whoIsOnCall", requestSent.getUrl())
    }

    @Test
    public void testWhoIsOnCallThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "whoIsOnCall", new WhoIsOnCallRequest(id: "oncall"))
    }

    @Test
    public void testListWhoIsOnCallSuccessfully() throws Exception {
        Map oncall1Content = new HashMap();
        oncall1Content.put(TestConstants.API.NAME, "schedule1");
        oncall1Content.put(TestConstants.API.PARTICIPANTS, [
                [name: "group1", type: "group"],
                [name: "user1@xyz.com", type: "user", forwarded: false],
                [name: "user2@xyz.com", type: "user", forwarded: true],
                [name: "escalation1", type: "escalation", participants: [[name: "schedule2", type: "schedule"]]],
                [name: "tim", type: "team"]
        ]);
        Map oncall2Content = new HashMap();
        oncall2Content.put(TestConstants.API.NAME, "schedule2");
        oncall2Content.put(TestConstants.API.PARTICIPANTS, [
                [name: "group2", type: "group"],
        ]);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson([oncalls: [oncall1Content, oncall2Content]]).getBytes(), 200, "application/json; charset=utf-8"))

        ListWhoIsOnCallRequest request = new ListWhoIsOnCallRequest();
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().listWhoIsOnCall(request)

        def whoIsOnCall = response.whoIsOnCallList.find { it.name == "schedule1" }

        assertEquals(5, whoIsOnCall.getParticipants().size())
        def escalationData = whoIsOnCall.getParticipants().find { it.name == "escalation1" }
        assertNotNull(escalationData)
        assertEquals(1, escalationData.participants.size())
        assertEquals("schedule2", escalationData.participants[0].name)

        int index = 0;
        oncall1Content[TestConstants.API.PARTICIPANTS].each {
            assertEquals(it.getAt("name"), whoIsOnCall.participants[index].name)
            assertEquals(it.getAt("type"), whoIsOnCall.participants[index].type)
            if (it.getAt("type") == "user") {
                assertEquals(it.getAt("forwarded"), whoIsOnCall.participants[index].forwarded)
            }
            index++;
        }

        whoIsOnCall = response.whoIsOnCallList.find { it.name == "schedule2" }
        assertEquals(1, whoIsOnCall.participants.size())
        assertEquals("group2", whoIsOnCall.participants[0].name)
        assertEquals("group", whoIsOnCall.participants[0].type)

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals("false", requestSent.getParameters()[TestConstants.API.FLAT])
        assertEquals("/v1.1/json/schedule/whoIsOnCall", requestSent.getUrl())
    }

    @Test
    public void testFlatListWhoIsOnCallSuccessfully() throws Exception {
        Map oncall1Content = new HashMap();
        def oncall1Recipients = [
                "user1@xyz.com",
                "user2@xyz.com"
        ]
        oncall1Content.put(TestConstants.API.NAME, "schedule1");
        oncall1Content.put(TestConstants.API.RECIPIENTS, oncall1Recipients);

        Map oncall2Content = new HashMap();
        def oncall2Recipients = [
                "user3@xyz.com",
                "user4@xyz.com"
        ]
        oncall2Content.put(TestConstants.API.NAME, "schedule2");
        oncall2Content.put(TestConstants.API.RECIPIENTS, oncall2Recipients);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson([oncalls: [oncall1Content, oncall2Content]]).getBytes(), 200, "application/json; charset=utf-8"))

        ListFlatWhoIsOnCallRequest request = new ListFlatWhoIsOnCallRequest();
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().listFlatWhoIsOnCall(request)

        def whoIsOnCall = response.whoIsOnCallList.find { it.name == "schedule1" }
        assertEquals(oncall1Recipients, whoIsOnCall.recipients)

        whoIsOnCall = response.whoIsOnCallList.find { it.name == "schedule2" }
        assertEquals(oncall2Recipients, whoIsOnCall.recipients)

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals("true", requestSent.getParameters()[TestConstants.API.FLAT])
        assertEquals("/v1.1/json/schedule/whoIsOnCall", requestSent.getUrl())
    }

    @Test
    public void testListWhoIsOnCallThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "listWhoIsOnCall", new ListWhoIsOnCallRequest())
    }


    @Test
    public void testListSchedulesSuccessfully() throws Exception {
        Map schedule1Content = new HashMap();
        schedule1Content.put(TestConstants.API.ID, "schedule1id");
        schedule1Content.put(TestConstants.API.NAME, "schedule1");
        schedule1Content.put(TestConstants.API.RULES, [
                [
                        id: "r1",
                        name: "rule1",
                        startDate   : "2013-02-25 22:00", rotationType: "weekly",
                        participants: [
                                [participant: "group1", type: "group"]
                        ],
                ]
        ]);
        Map schedule2Content = new HashMap();
        schedule2Content.put(TestConstants.API.ID, "schedule2id");
        schedule2Content.put(TestConstants.API.NAME, "schedule2");
        schedule2Content.put(TestConstants.API.ENABLED, true);
        schedule2Content.put(TestConstants.API.RULES, [
                [
                        id: "r2",
                        startDate   : "2013-02-25 22:00", rotationType: "daily",
                        participants: [
                                [participant: "escalation1", type: "escalation"]
                        ],
                ]
        ]);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson(schedules: [schedule1Content, schedule2Content]).getBytes(), 200, "application/json; charset=utf-8"))

        ListSchedulesRequest request = new ListSchedulesRequest();
        request.setApiKey("customer1");

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().listSchedules(request)
        assertEquals(2, response.schedules.size())
        //check schedule 1
        Schedule schedule = response.getSchedules().find { it.id == schedule1Content[TestConstants.API.ID] }
        assertEquals(schedule1Content[TestConstants.API.NAME], schedule.name)
        assertEquals(schedule1Content[TestConstants.API.ID], schedule.id)
        assertNull(schedule.isEnabled())
        assertEquals(1, schedule.rotations.size())


        assertEquals(ScheduleRotation.RotationType.weekly, schedule.rotations[0].rotationType)
        assertEquals(sdf.parse("2013-02-25 22:00"), schedule.rotations[0].startDate)
        assertEquals("rule1", schedule.rotations[0].name)
        assertEquals("r1", schedule.rotations[0].id)
        assertEquals(1, schedule.rotations[0].getParticipants().size())

        assertEquals("group1", schedule.rotations[0].getParticipants()[0].getParticipant())
        assertEquals(ScheduleParticipant.Type.group.name(), schedule.rotations[0].getParticipants()[0].getType().name())

        //check schedule2
        schedule = response.getSchedules().find { it.id == schedule2Content[TestConstants.API.ID] }
        assertEquals(schedule2Content[TestConstants.API.NAME], schedule.name)
        assertEquals(schedule2Content[TestConstants.API.ID], schedule.id)
        assertEquals(schedule2Content[TestConstants.API.ENABLED], schedule.isEnabled())
        assertEquals(1, schedule.rotations.size())

        assertNull(schedule.rotations[0].name)
        assertEquals("r2",schedule.rotations[0].id)
        assertEquals(ScheduleRotation.RotationType.daily, schedule.rotations[0].rotationType)
        assertEquals(sdf.parse("2013-02-25 22:00"), schedule.rotations[0].startDate)
        assertEquals(1, schedule.rotations[0].getParticipants().size())

        assertEquals("escalation1", schedule.rotations[0].getParticipants()[0].getParticipant())
        assertEquals(ScheduleParticipant.Type.escalation.name(), schedule.rotations[0].getParticipants()[0].getType().name())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals("/v1/json/schedule", requestSent.getUrl())
    }

    @Test
    public void testListSchedulesThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "listSchedules", new ListSchedulesRequest())
    }


    @Test
    public void testAddScheduleOverrideSuccessfully() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"alias\":\"alias1\", \"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        AddScheduleOverrideRequest request = new AddScheduleOverrideRequest();
        request.setApiKey("customer1");
        request.setAlias("override1");
        request.setSchedule("schedule1");
        request.setStartDate(new Date());
        request.setUser("user1");
        request.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        request.setEndDate(new Date(System.currentTimeMillis() + 100000000l));
        request.setRotationIds(["rotation1"])

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().addScheduleOverride(request)
        assertEquals("alias1", response.getAlias())
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpPost.METHOD_NAME, requestSent.getMethod());
        assertEquals("/v1/json/schedule/override", requestSent.getUrl())
        assertEquals("application/json; charset=utf-8", requestSent.getHeader(HttpHeaders.CONTENT_TYPE));

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(request.getTimeZone());

        def jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertEquals(request.getAlias(), jsonContent[TestConstants.API.ALIAS])
        assertEquals(sdf.format(request.getStartDate()), jsonContent[TestConstants.API.START_DATE])
        assertEquals(sdf.format(request.getEndDate()), jsonContent[TestConstants.API.END_DATE])
        assertEquals(request.getUser(), jsonContent[TestConstants.API.USER])
        assertEquals(request.getSchedule(), jsonContent[TestConstants.API.SCHEDULE])
        assertEquals(request.getTimeZone().getID(), jsonContent[TestConstants.API.TIMEZONE])
        assertEquals(request.getRotationIds(), jsonContent[TestConstants.API.ROTATION_IDS])
    }

    @Test
    public void testAddScheduleOverrideThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "addScheduleOverride", new AddScheduleOverrideRequest())
    }

    @Test
    public void testUpdateScheduleOverrideSuccessfully() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"alias\":\"alias1\", \"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        UpdateScheduleOverrideRequest request = new UpdateScheduleOverrideRequest();
        request.setApiKey("customer1");
        request.setAlias("alias1");
        request.setSchedule("schedule1");
        request.setStartDate(new Date());
        request.setUser("user1");
        request.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        request.setEndDate(new Date(System.currentTimeMillis() + 100000000l));
        request.setRotationIds(["rot1"])

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().updateScheduleOverride(request)
        assertEquals("alias1", response.getAlias())
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpPost.METHOD_NAME, requestSent.getMethod());
        assertEquals("/v1/json/schedule/override", requestSent.getUrl())
        assertEquals("application/json; charset=utf-8", requestSent.getHeader(HttpHeaders.CONTENT_TYPE));

        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(request.getTimeZone());

        def jsonContent = JsonUtils.parse(requestSent.getContentAsByte())
        assertEquals(request.getApiKey(), jsonContent[TestConstants.API.API_KEY])
        assertEquals(request.getAlias(), jsonContent[TestConstants.API.ALIAS])
        assertEquals(sdf.format(request.getStartDate()), jsonContent[TestConstants.API.START_DATE])
        assertEquals(sdf.format(request.getEndDate()), jsonContent[TestConstants.API.END_DATE])
        assertEquals(request.getUser(), jsonContent[TestConstants.API.USER])
        assertEquals(request.getSchedule(), jsonContent[TestConstants.API.SCHEDULE])
        assertEquals(request.getTimeZone().getID(), jsonContent[TestConstants.API.TIMEZONE])
        assertEquals(request.getRotationIds(), jsonContent[TestConstants.API.ROTATION_IDS])
    }

    @Test
    public void testUpdateScheduleOverrideThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "updateScheduleOverride", new UpdateScheduleOverrideRequest())
    }

    @Test
    public void testDeleteScheduleOverrideSuccessfully() throws Exception {
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse("{\"took\":1}".getBytes(), 200, "application/json; charset=utf-8"))

        DeleteScheduleOverrideRequest request = new DeleteScheduleOverrideRequest();
        request.setAlias("alias1");
        request.setSchedule("schedule1");
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().deleteScheduleOverride(request)
        assertEquals(1, response.getTook())

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]

        assertEquals(HttpDelete.METHOD_NAME, requestSent.getMethod());
        assertEquals("/v1/json/schedule/override", requestSent.getUrl())

        assertEquals(request.getAlias(), requestSent.getParameters()[TestConstants.API.ALIAS]);
        assertEquals(request.getSchedule(), requestSent.getParameters()[TestConstants.API.SCHEDULE]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
    }

    @Test
    public void testDeleteScheduleOverrideThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "deleteScheduleOverride", new DeleteScheduleOverrideRequest())
    }

    @Test
    public void testGetScheduleOverrideSuccessfully() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        def startDate = new Date()
        def endDate = new Date(System.currentTimeMillis() + 1000000)

        Map jsonContent = new HashMap();
        jsonContent.put("took", 1);
        jsonContent.put(TestConstants.API.ALIAS, "alias1");
        jsonContent.put(TestConstants.API.START_DATE, sdf.format(startDate));
        jsonContent.put(TestConstants.API.END_DATE, sdf.format(endDate));
        jsonContent.put(TestConstants.API.USER, "user1");
        jsonContent.put(TestConstants.API.TIMEZONE, sdf.getTimeZone().getID());
        jsonContent.put(TestConstants.API.ROTATION_IDS, ["rot1"]);
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson(jsonContent).getBytes(), 200, "application/json; charset=utf-8"))

        GetScheduleOverrideRequest request = new GetScheduleOverrideRequest();
        request.setAlias("alias1");
        request.setSchedule("schedule1");
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().getScheduleOverride(request)
        assertEquals(1, response.getTook())
        assertEquals(jsonContent[TestConstants.API.ALIAS], response.getScheduleOverride().alias)
        assertEquals(jsonContent[TestConstants.API.ROTATION_IDS], response.getScheduleOverride().rotationIds)
        assertEquals(jsonContent[TestConstants.API.USER], response.getScheduleOverride().user)
        assertEquals(sdf.format(startDate), sdf.format(response.getScheduleOverride().startDate))
        assertEquals(sdf.format(endDate), sdf.format(response.getScheduleOverride().endDate))
        assertEquals(sdf.getTimeZone(), response.getScheduleOverride().timeZone)

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getAlias(), requestSent.getParameters()[TestConstants.API.ALIAS]);
        assertEquals(request.getSchedule(), requestSent.getParameters()[TestConstants.API.SCHEDULE]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals("/v1/json/schedule/override", requestSent.getUrl())
    }

    @Test
    public void testGetScheduleOverrideThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "getScheduleOverride", new GetScheduleOverrideRequest())
    }

    @Test
    public void testListScheduleOverridesSuccessfully() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(TestConstants.Common.API_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        def startDate1 = new Date()
        def endDate1 = new Date(System.currentTimeMillis() + 1000000)
        def startDate2 = new Date(System.currentTimeMillis() + 2000000)
        def endDate2 = new Date(System.currentTimeMillis() + 3000000)

        Map jsonContent1 = new HashMap();
        jsonContent1.put(TestConstants.API.START_DATE, sdf.format(startDate1));
        jsonContent1.put(TestConstants.API.END_DATE, sdf.format(endDate1));
        jsonContent1.put(TestConstants.API.USER, "user1");
        jsonContent1.put(TestConstants.API.ALIAS, "alias1");
        jsonContent1.put(TestConstants.API.TIMEZONE, sdf.getTimeZone().getID());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        Map jsonContent2 = new HashMap();
        jsonContent2.put(TestConstants.API.START_DATE, sdf.format(startDate2));
        jsonContent2.put(TestConstants.API.END_DATE, sdf.format(endDate2));
        jsonContent2.put(TestConstants.API.USER, "user2");
        jsonContent2.put(TestConstants.API.ALIAS, "alias2");
        jsonContent2.put(TestConstants.API.TIMEZONE, sdf.getTimeZone().getID());
        OpsGenieClientTestCase.httpServer.setResponseToReturn(new HttpTestResponse(JsonUtils.toJson([took: 1, overrides: [jsonContent1, jsonContent2]]).getBytes(), 200, "application/json; charset=utf-8"))

        ListScheduleOverridesRequest request = new ListScheduleOverridesRequest();
        request.setSchedule("schedule1");
        request.setApiKey("customer1");

        def response = OpsGenieClientTestCase.opsgenieClient.schedule().listScheduleOverrides(request)
        assertEquals(1, response.getTook())
        def overrides = response.getScheduleOverides();
        assertEquals(2, overrides.size())
        def override = overrides.find { it.alias == jsonContent1[TestConstants.API.ALIAS] }
        assertEquals(jsonContent1[TestConstants.API.USER], override.user)
        assertEquals(sdf.format(startDate1), sdf.format(override.startDate))
        assertEquals(sdf.format(endDate1), sdf.format(override.endDate))
        assertEquals(TimeZone.getTimeZone("GMT-5"), override.timeZone)

        override = overrides.find { it.alias == jsonContent2[TestConstants.API.ALIAS] }
        assertEquals(jsonContent2[TestConstants.API.USER], override.user)
        assertEquals(sdf.format(startDate2), sdf.format(override.startDate))
        assertEquals(sdf.format(endDate2), sdf.format(override.endDate))
        assertEquals(TimeZone.getTimeZone("GMT-3"), override.timeZone)

        assertEquals(1, receivedRequests.size());
        HttpTestRequest requestSent = receivedRequests[0]
        assertEquals(HttpGet.METHOD_NAME, requestSent.getMethod());
        assertEquals(request.getSchedule(), requestSent.getParameters()[TestConstants.API.SCHEDULE]);
        assertEquals(request.getApiKey(), requestSent.getParameters()[TestConstants.API.API_KEY])
        assertEquals("/v1/json/schedule/override", requestSent.getUrl())
    }

    @Test
    public void testListScheduleOverridesThrowsExceptionIfRequestCannotBeValidated() throws Exception {
        _testThrowsExceptionIfRequestCannotBeValidated(OpsGenieClientTestCase.opsgenieClient.schedule(), "listScheduleOverrides", new ListScheduleOverridesRequest())
    }


}

