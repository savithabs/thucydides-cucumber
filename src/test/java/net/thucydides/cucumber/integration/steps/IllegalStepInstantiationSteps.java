package net.thucydides.cucumber.integration.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Steps;
import net.thucydides.cucumber.integration.steps.thucydides.SampleSteps;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by john on 15/07/2014.
 */
public class IllegalStepInstantiationSteps {

    @Steps
    SampleSteps sampleSteps;

    public IllegalStepInstantiationSteps(SampleSteps sampleSteps) {
        this.sampleSteps = sampleSteps;
    }

    @Given("I have a step library without a default constructor")
    public void featureFileContainsStepsFields() {
    }

    @Then("the tests should fail with an exception")
    public void thePageObjectsShouldBeInstantiated() {
        assertThat(sampleSteps.pageObject).isNotNull();
    }

}
