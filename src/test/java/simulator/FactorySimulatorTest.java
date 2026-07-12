package simulator;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

// Allure annotations here are not mandatory, added just since using it in all test frameworks
class FactorySimulatorTest {

    @Test
    @Story("Machine startup")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Machine with no startup delay should immediately transition to WORKING state")
    void testMachineWithNoDelayStartsWorking() {
        FakeApi api = new FakeApi();
        FactorySimulator simulator = new FactorySimulator(api, 9);
        simulator.start();

        Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WORKING);
        Assert.assertEquals(simulator.getMachines().get(1).getState(), State.WAITING_FOR_START);
        Assert.assertEquals(simulator.getMachines().get(2).getState(), State.WAITING_FOR_START);
        }


    @Test
    @Story("Product delivery")
    @Severity(SeverityLevel.CRITICAL)
    @Description("After completion, machine should set the full-place sensor and return to WAITING_FOR_EMPTY")
    void testMachineDeliversProductAfterWorking()  {
            FakeApi api = new FakeApi();
            FactorySimulator simulator = new FactorySimulator(api, 9);
            simulator.start();

            api.tick(9);

            Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WAITING_FOR_EMPTY);
            Assert.assertTrue(api.getFullPlaceSensor());
    }


    @Test
    @Story("Product delivery")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Machine should wait in WAITING_FOR_FULL when full place is occupied, and deliver once it becomes free")
    void machineWaitsWhenFullPlaceOccupiedThenDeliversWhenFree() {
        FakeApi api = new FakeApi();
        FactorySimulator simulator = new FactorySimulator(api, 9);
        simulator.start();

        api.setFullPlaceSensor(true);
        api.tick(9);

        Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WAITING_FOR_FULL);

        api.setFullPlaceSensor(false); // full place freed, observer fires
        Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WAITING_FOR_EMPTY);
    }

    @Test
    @Story("Container handling")
    @Severity(SeverityLevel.CRITICAL)
    @Description("When two machines are both waiting for an empty container, only the first takes it")
    void onlyOneMachineTakesContainerWhenTwoAreWaiting() {
            FakeApi api = new FakeApi();
            api.setEmptyPlaceSensor(false);
            FactorySimulator simulator = new FactorySimulator(api, 9);
            simulator.start();

            // No container for machine 1
            Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WAITING_FOR_EMPTY);

            // No container for first and second machine still
            api.tick(3);
            Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WAITING_FOR_EMPTY);
            Assert.assertEquals(simulator.getMachines().get(1).getState(), State.WAITING_FOR_EMPTY);

            // Container arrives, work is starting
            api.setEmptyPlaceSensor(true);
            Assert.assertEquals(simulator.getMachines().get(0).getState(), State.WORKING);
            Assert.assertEquals(simulator.getMachines().get(1).getState(), State.WAITING_FOR_EMPTY);
    }

    @Test
    @Story("Simulator lifecycle")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Calling stop() cancels timer")
    void stopCancelsTimerAndNoStart()  {
            FakeApi api = new FakeApi();
            FactorySimulator simulator = new FactorySimulator(api, 9);
            simulator.start();

            simulator.stop();
            api.tick(3); // machine 2 delay expired, but still in WAITING_FOR_START

            Assert.assertEquals(simulator.getMachines().get(1).getState(), State.WAITING_FOR_START);
    }

}
