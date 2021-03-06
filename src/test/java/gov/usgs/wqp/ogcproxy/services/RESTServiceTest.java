package gov.usgs.wqp.ogcproxy.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.usgs.wqp.ogcproxy.model.DynamicLayer;
import gov.usgs.wqp.ogcproxy.model.OGCRequest;
import gov.usgs.wqp.ogcproxy.model.ogc.services.OGCServices;
import gov.usgs.wqp.ogcproxy.model.parameters.ProxyDataSourceParameter;
import gov.usgs.wqp.ogcproxy.services.wqp.WQPDynamicLayerCachingService;
import java.util.Map;

public class RESTServiceTest {

	@Mock
	private WQPDynamicLayerCachingService layerCachingService;

	private RESTService service;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		service = new RESTService(layerCachingService);
	}

	@Test
	public void checkCacheStatusOkTest() throws InterruptedException, ExecutionException {
		Collection<DynamicLayer> cache = new ArrayList<>();
		cache.add(new DynamicLayer(new OGCRequest(OGCServices.WMS), "abcWorkspace"));
		when(layerCachingService.getCacheValues()).thenReturn(cache);

		Map<String, Object> mv = service.checkCacheStatus(ProxyDataSourceParameter.WQP_SITES.toString());
		assertTrue(mv.containsKey("site"));
		assertEquals("WQP Layer Building Service", mv.get("site"));
		assertTrue(mv.containsKey("cache"));
		assertEquals(cache, mv.get("cache"));
	}

	@Test
	public void checkCacheStatusBadTest() {
		Map<String, Object> mv = service.checkCacheStatus("no_sites_here");
		assertTrue(mv.containsKey("site"));
		assertEquals("no_sites_here", mv.get("site"));
	}

	@Test
	public void clearCacheBySiteOkTest() {
		when(layerCachingService.clearCache()).thenReturn(5);

		assertTrue(service.clearCacheBySite(ProxyDataSourceParameter.WQP_SITES.toString()));
	}

	@Test
	public void clearCacheBySiteBadTest() {
		assertFalse(service.clearCacheBySite("no_sites_here"));
	}

}
