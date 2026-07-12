# Test task implementation

## Design

The simulator models three independent machines sharing two resources:

* empty container place
* finished product place

Each machine is implemented as a small machine state (started with booleans, but implementation was much dirtier).

## Assumptions and trade-offs

-Simplfication: sequental callbacks execution.
If the real control system delivered callbacks asynchronously, access to the shared resources and machine states would need synchronization.
- Some classes could be merged into others (e.g. `Buffer`), but for machines possibly better not to have direct access to the shared resource coordination.
- Using `FakeApi.tick()` instead of asynchronous timers.

## Testing

I did not aim to reach full coverage here, but added a couple of tests.
Allure may sound like overkill here, as it is much more suitable for UI tests, where screenshots and logs add more value.

## How to run

```bash
mvn test
```
