package simulator;

public class Main {

    public static void main(String[] args) throws Exception {
            FakeApi api = new FakeApi();
            FactorySimulator simulator = new FactorySimulator(api, 9);
            simulator.start();

            for (int i = 0; i < 8; i++) {
                api.tick(2);
                api.setFullPlaceSensor(true);

                api.tick(2);
                api.setFullPlaceSensor(false);
            }

            simulator.stop();
    }
}