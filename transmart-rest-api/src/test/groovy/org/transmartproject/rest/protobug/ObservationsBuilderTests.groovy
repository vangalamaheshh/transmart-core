package org.transmartproject.rest.protobug

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.db.clinical.MultidimensionalDataResourceService
import org.transmartproject.db.dataquery.clinical.ClinicalTestData
import org.transmartproject.db.dataquery2.DimensionImpl
import org.transmartproject.db.dataquery2.query.Constraint
import org.transmartproject.db.dataquery2.query.StudyConstraint
import org.transmartproject.db.metadata.DimensionDescription
import org.transmartproject.db.TestData
import org.transmartproject.rest.hypercubeProto.ObservationsProto
import org.transmartproject.rest.protobuf.ObservationsSerializer
import spock.lang.Specification

import static spock.util.matcher.HamcrestSupport.that
import static org.hamcrest.Matchers.*


/**
 * Created by piotrzakrzewski on 02/11/2016.
 */
@Integration
@Rollback
@Slf4j
class ObservationsBuilderTests extends Specification {

    TestData testData
    ClinicalTestData clinicalData
    Map<String, DimensionImpl> dims
    
    @Autowired
    MultidimensionalDataResourceService queryResource

    public void testJsonSerialization() {
        setupData()
        Constraint constraint = new StudyConstraint(studyId: clinicalData.longitudinalStudy.studyId)
        def mockedCube = queryResource.retrieveData('clinical', [clinicalData.longitudinalStudy], constraint: constraint)
        def builder = new ObservationsSerializer(mockedCube, ObservationsSerializer.Format.JSON)

        when:
        def out = new ByteArrayOutputStream()
        builder.write(out)
        out.flush()
        Collection result = new JsonSlurper().parse(out.toByteArray())
        def dimElementsSize = result.last()['dimension'].size()

        then:
        result.size() == 14
        that result, everyItem(anyOf(
                hasKey('dimensionDeclarations'),
                hasKey('dimensionIndexes'),
                hasKey('dimension')
        ))
        that result.first()['dimensionDeclarations'], hasSize(mockedCube.dimensions.size())
        that result.first()['dimensionDeclarations']['name'],
                containsInAnyOrder(mockedCube.dimensions.collect{it.toString()}.toArray()
                )
        that result['dimension'].findAll(), everyItem(hasSize(dimElementsSize))
        that result['dimensionIndexes'].findAll(), everyItem(hasSize(dimElementsSize))
    }

    public void testProtobufSerialization() {
        setupData()
        Constraint constraint = new StudyConstraint(studyId: clinicalData.longitudinalStudy.studyId)
        def mockedCube = queryResource.retrieveData('clinical', [clinicalData.longitudinalStudy], constraint: constraint)
        def builder = new ObservationsSerializer(mockedCube, ObservationsSerializer.Format.PROTOBUF)

        when:
        def s_out = new ByteArrayOutputStream()
        builder.write(s_out)
        s_out.flush()
        def data = s_out.toByteArray()
        log.info "OUTPUT: ${s_out.toString()}"

        then:
        data != null
        data.length > 0

        when:
        def s_in = new ByteArrayInputStream(data)
        log.info "Reading header..."
        def header = ObservationsProto.Header.parseDelimitedFrom(s_in)
        def cells = []
        int count = 0
        while(true) {
            count++
            if (count > 12) {
                throw new Exception("Expected previous message to be marked as 'last'.")
            }
            log.info "Reading cell..."
            def cell = ObservationsProto.Observation.parseDelimitedFrom(s_in)
            cells << cell
            if (cell.last) {
                log.info "Last cell."
                break
            }
        }
        log.info "Reading footer..."
        def footer = ObservationsProto.Footer.parseDelimitedFrom(s_in)

        then:
        header != null
        header.dimensionDeclarationsList.size() == mockedCube.dimensions.size()
        cells.size() == 12
        footer != null
    }

    void setupData() {
        testData = TestData.createHypercubeDefault()
        clinicalData = testData.clinicalData
        testData.saveAll()
        dims = DimensionDescription.dimensionsMap
    }
}