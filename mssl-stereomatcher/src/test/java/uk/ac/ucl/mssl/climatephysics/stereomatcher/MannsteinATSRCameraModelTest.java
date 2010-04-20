package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MannsteinATSRCameraModelTest extends TestCase {
	public void testAngularDistances(){
		MannsteinATSRCameraModel model = new MannsteinATSRCameraModel(22d);
		double[] angularDistances = model.angularDistances();
		Assert.assertEquals(angularDistances[0], -0.040103594, 0.00001d);
		Assert.assertEquals(angularDistances[511], 0.040103594, 0.00001d);
		Assert.assertEquals(angularDistances[45], -0.033040339, 0.00001d);
	}
	
	public void testParameterisedRotationAngle(){
		MannsteinATSRCameraModel model = new MannsteinATSRCameraModel(22d);
		double[] eps = model.parameterisedRotationAngle();

		Assert.assertEquals(eps[0],0.0, 0.0001);
        Assert.assertEquals(eps[1],0.0062831853, 0.0001);
        Assert.assertEquals(eps[2],0.012566371, 0.0001);
        Assert.assertEquals(eps[34],0.21362830, 0.0001);;
        Assert.assertEquals(eps[0], 0.00000000000000000000000000000000000, 0.0001);
        Assert.assertEquals(eps[1], 0.00628318530717958661363509165198593, 0.0001);
        Assert.assertEquals(eps[2], 0.01256637061435917322727018330397186, 0.0001);
        Assert.assertEquals(eps[3], 0.01884955592153875897354353696755425, 0.0001);
        Assert.assertEquals(eps[4], 0.02513274122871834645454036660794372, 0.0001);
        Assert.assertEquals(eps[5], 0.03141592653589793393553719624833320, 0.0001);
        Assert.assertEquals(eps[6], 0.03769911184307751794708707393510849, 0.0001);
        Assert.assertEquals(eps[7], 0.04398229715025710195863695162188378, 0.0001);
        Assert.assertEquals(eps[8], 0.05026548245743669290908073321588745, 0.0001);
        Assert.assertEquals(eps[9], 0.05654866776461627692063061090266274, 0.0001);
        Assert.assertEquals(eps[10], 0.06283185307179586787107439249666641, 0.0001);
        Assert.assertEquals(eps[11], 0.06911503837897543800483646236898494, 0.0001);
        Assert.assertEquals(eps[12], 0.07539822368615503589417414787021698, 0.0001);
        Assert.assertEquals(eps[13], 0.08168140899333463378351183337144903, 0.0001);
        Assert.assertEquals(eps[14], 0.08796459430051420391727390324376756, 0.0001);
        Assert.assertEquals(eps[15], 0.09424777960769378792882378093054285, 0.0001);
        Assert.assertEquals(eps[16], 0.10053096491487338581816146643177490, 0.0001);
        Assert.assertEquals(eps[17], 0.10681415022205296982971134411855019, 0.0001);
        Assert.assertEquals(eps[18], 0.11309733552923255384126122180532548, 0.0001);
        Assert.assertEquals(eps[19], 0.11938052083641213785281109949210077, 0.0001);

	}

	public void testComputeAngles(){
		MannsteinATSRCameraModel model = new MannsteinATSRCameraModel(23.627d*Math.PI/180.0d);
		double[] eps = model.parameterisedRotationAngle();
	}
}
