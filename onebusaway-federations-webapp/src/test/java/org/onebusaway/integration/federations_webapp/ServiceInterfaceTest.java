package org.onebusaway.integration.federations_webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.federations.FederatedServiceRegistry;
import org.onebusaway.federations.FederatedServiceRegistryEntry;
import org.onebusaway.federations.impl.FederatedServiceRegistryImpl;

import com.caucho.hessian.client.HessianProxyFactory;

public class ServiceInterfaceTest {

  private FederatedServiceRegistry _service;

  @Before
  public void setup() throws MalformedURLException {
    HessianProxyFactory factory = new HessianProxyFactory();
    _service = (FederatedServiceRegistry) factory.create(
        FederatedServiceRegistry.class,
        "http://localhost:8080/onebusaway-federations-webapp/remoting/service-registry");
  }

  @After
  public void teardown() {
    if (_service != null)
      _service.removeAllServices();
  }

  @Test
  public void test01() {
    _service.addService("a1", TestA.class.getName(), map("type", "test"));
    _service.addService("a2", TestA.class.getName(), map("type", "prod"));
    _service.addService("b1", TestB.class.getName(), map("type", "test"));
    _service.addService("b2", TestB.class.getName(), map("type", "prod"));

    List<String> urls = getServiceUrls(_service, TestA.class, map());
    assertEquals(2, urls.size());
    assertTrue(urls.contains("a1"));
    assertTrue(urls.contains("a2"));

    urls = getServiceUrls(_service, TestA.class, map("type", "test"));
    assertEquals(1, urls.size());
    assertTrue(urls.contains("a1"));

    urls = getServiceUrls(_service, TestA.class, map("type", "prod"));
    assertEquals(1, urls.size());
    assertTrue(urls.contains("a2"));

    urls = getServiceUrls(_service, TestA.class, map("type", "what"));
    assertEquals(0, urls.size());

    urls = getServiceUrls(_service, TestB.class, map());
    assertEquals(2, urls.size());
    assertTrue(urls.contains("b1"));
    assertTrue(urls.contains("b2"));

    _service.setServiceStatus("a1", false);

    urls = getServiceUrls(_service, TestA.class, map());
    assertEquals(1, urls.size());
    assertTrue(urls.contains("a2"));

    urls = getServiceUrls(_service, TestA.class, map("type", "test"));
    assertEquals(0, urls.size());

    _service.setServiceStatus("a1", true);

    urls = getServiceUrls(_service, TestA.class, map());
    assertEquals(2, urls.size());
    assertTrue(urls.contains("a1"));
    assertTrue(urls.contains("a2"));

    urls = getServiceUrls(_service, TestA.class, map("type", "test"));
    assertEquals(1, urls.size());
    assertTrue(urls.contains("a1"));

    _service.removeService("b1");

    urls = getServiceUrls(_service, TestB.class, map());
    assertEquals(1, urls.size());
    assertTrue(urls.contains("b2"));

    urls = getServiceUrls(_service, TestB.class, map("type", "test"));
    assertEquals(0, urls.size());

    _service.removeAllServices();

    List<FederatedServiceRegistryEntry> services = _service.getAllServices();
    assertTrue(services.isEmpty());
  }

  @Test
  public void testReregistration() {

    FederatedServiceRegistryImpl registry = new FederatedServiceRegistryImpl();
    registry.addService("a1", TestA.class.getName(), map());
    registry.setServiceStatus("a1", false);

    List<FederatedServiceRegistryEntry> services = registry.getServices(
        TestA.class.getName(), map());
    assertEquals(0, services.size());

    registry.addService("a1", TestA.class.getName(), map());

    services = registry.getServices(TestA.class.getName(), map());
    assertEquals(0, services.size());

    registry.setServiceStatus("a1", true);

    services = registry.getServices(TestA.class.getName(), map());
    assertEquals(1, services.size());
  }

  private List<String> getServiceUrls(FederatedServiceRegistry registry,
      Class<?> serviceClass, Map<String, String> properties) {
    List<FederatedServiceRegistryEntry> entries = registry.getServices(
        serviceClass.getName(), properties);
    List<String> urls = new ArrayList<String>();
    for (FederatedServiceRegistryEntry entry : entries)
      urls.add(entry.getServiceUrl());
    Collections.sort(urls);
    return urls;
  }

  private Map<String, String> map(String... values) {

    if (values.length % 2 != 0)
      throw new IllegalArgumentException(
          "expected even number of key-value pairs");

    Map<String, String> m = new HashMap<String, String>();
    for (int i = 0; i < values.length; i += 2)
      m.put(values[i], values[i + 1]);

    return m;
  }

  private static interface TestA {

  }

  private static interface TestB {

  }
}
