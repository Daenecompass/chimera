package de.hpi.bpt.chimera.parser.fragment.bpmn.unmarshaller.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to store parallel Gateway. An exclusive gateway can have an arbitrary number
 * of incoming control flows and one outgoing control flow.
 * Example String:
 * <bpmn:parallelGateway id="ParallelGateway_1kginh1">
 * <bpmn:incoming>SequenceFlow_08t16y9</bpmn:incoming>
 * <bpmn:incoming>SequenceFlow_1llhwid</bpmn:incoming>
 * <bpmn:outgoing>SequenceFlow_08rfwur</bpmn:outgoing>
 * </bpmn:parallelGateway>
 */
@XmlRootElement(name = "bpmn:parallelGateway")
@XmlAccessorType(XmlAccessType.NONE)
public class ParallelGateway extends AbstractControlNode {

}
