package my.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ExtensionType;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class TLSSocketConnectionFactory extends SSLSocketFactory {

	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	@Override
	public Socket createSocket(Socket socket, final String host, int port,
			boolean arg3) throws IOException {
		if (socket == null) {
			socket = new Socket();
		}
		if (!socket.isConnected()) {
			socket.connect(new InetSocketAddress(host, port));
		}

		final TlsClientProtocol tlsClientProtocol = new     TlsClientProtocol(socket.getInputStream(), socket.getOutputStream(), new     SecureRandom());

		return _createSSLSocket(host, tlsClientProtocol);
	}

	@Override public String[] getDefaultCipherSuites() { return null; }
	@Override public String[] getSupportedCipherSuites() { return null; }
	@Override public Socket createSocket(String host, int port) throws IOException, UnknownHostException { throw new UnsupportedOperationException(); }
	@Override public Socket createSocket(InetAddress host, int port) throws IOException { throw new UnsupportedOperationException(); }
	@Override public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException { return null; }
	@Override public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException { throw new UnsupportedOperationException(); }

	private SSLSocket _createSSLSocket(final String host, final TlsClientProtocol tlsClientProtocol) {
		return new SSLSocket() {
			private java.security.cert.Certificate[] peertCerts;

			@Override public InputStream getInputStream() throws IOException { return tlsClientProtocol.getInputStream(); }
			@Override public OutputStream getOutputStream() throws IOException { return tlsClientProtocol.getOutputStream(); }
			@Override public synchronized void close() throws IOException { tlsClientProtocol.close(); }
			@Override public void addHandshakeCompletedListener( HandshakeCompletedListener arg0) { }
			@Override public boolean getEnableSessionCreation() { return false; }
			@Override public String[] getEnabledCipherSuites() { return null; }
			@Override public String[] getEnabledProtocols() { return null; }
			@Override public boolean getNeedClientAuth() { return false; }

			@Override
			public SSLSession getSession() {
				return new SSLSession() {

					@Override
					public int getApplicationBufferSize() {
						return 0;
					}

					@Override public String getCipherSuite() { return ""; }
					@Override public long getCreationTime() { throw new UnsupportedOperationException(); }
					@Override public byte[] getId() { throw new UnsupportedOperationException(); }
					@Override public long getLastAccessedTime() { throw new UnsupportedOperationException(); }
					@Override public java.security.cert.Certificate[] getLocalCertificates() { throw new UnsupportedOperationException(); }
					@Override public Principal getLocalPrincipal() { throw new UnsupportedOperationException(); }
					@Override public int getPacketBufferSize() { throw new UnsupportedOperationException(); }
					@Override public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException { return null; }
					@Override public java.security.cert.Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException { return peertCerts; }
					@Override public String getPeerHost() { throw new UnsupportedOperationException(); }
					@Override public int getPeerPort() { return 0; }
					@Override public Principal getPeerPrincipal() throws SSLPeerUnverifiedException { return null; }
					@Override public String getProtocol() { throw new UnsupportedOperationException(); }
					@Override public SSLSessionContext getSessionContext() { throw new UnsupportedOperationException(); }
					@Override public Object getValue(String arg0) { throw new UnsupportedOperationException(); }
					@Override public String[] getValueNames() { throw new UnsupportedOperationException(); }
					@Override public void invalidate() { throw new UnsupportedOperationException(); }
					@Override public boolean isValid() { throw new UnsupportedOperationException(); }
					@Override public void putValue(String arg0, Object arg1) { throw new UnsupportedOperationException(); }
					@Override public void removeValue(String arg0) { throw new UnsupportedOperationException(); }
				};
			}

			@Override public String[] getSupportedProtocols() { return null; }
			@Override public boolean getUseClientMode() { return false; }
			@Override public boolean getWantClientAuth() { return false; }
			@Override public void removeHandshakeCompletedListener(HandshakeCompletedListener arg0) { }
			@Override public void setEnableSessionCreation(boolean arg0) { }
			@Override public void setEnabledCipherSuites(String[] arg0) { }
			@Override public void setEnabledProtocols(String[] arg0) { }
			@Override public void setNeedClientAuth(boolean arg0) { }
			@Override public void setUseClientMode(boolean arg0) { }
			@Override public void setWantClientAuth(boolean arg0) { }
			@Override public String[] getSupportedCipherSuites() { return null; }

			@Override
			public void startHandshake() throws IOException {
				tlsClientProtocol.connect(new DefaultTlsClient() {

					@SuppressWarnings("unchecked")
					@Override
					public Hashtable<Integer, byte[]> getClientExtensions() throws IOException {
						Hashtable<Integer, byte[]> clientExtensions = super.getClientExtensions();
						if (clientExtensions == null) {
							clientExtensions = new Hashtable<Integer, byte[]>();
						}

						//Add host_name
						byte[] host_name = host.getBytes();

						final ByteArrayOutputStream baos = new ByteArrayOutputStream();
						final DataOutputStream dos = new DataOutputStream(baos);
						dos.writeShort(host_name.length + 3);
						dos.writeByte(0);
						dos.writeShort(host_name.length);
						dos.write(host_name);
						dos.close();
						clientExtensions.put(ExtensionType.server_name, baos.toByteArray());
						return clientExtensions;
					}

					@Override
					public TlsAuthentication getAuthentication() throws IOException {
						return new TlsAuthentication() {

							@Override
							public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
								try {
									KeyStore ks = loadKeyStore();

									CertificateFactory cf = CertificateFactory.getInstance("X.509");
									List<java.security.cert.Certificate> certs = new LinkedList<java.security.cert.Certificate>();
									boolean trustedCertificate = false;
									for ( org.bouncycastle.asn1.x509.Certificate c : serverCertificate.getCertificateList()) {
										java.security.cert.Certificate cert = cf.generateCertificate(new ByteArrayInputStream(c.getEncoded()));
										certs.add(cert);

										if (cert instanceof java.security.cert.X509Certificate) {
										    java.security.cert.X509Certificate x509cert = (java.security.cert.X509Certificate) cert;
										    String subjectDN = x509cert.getSubjectDN().getName();
										    String issuerDN = x509cert.getIssuerDN().getName();

										    if (subjectDN.equals(issuerDN)) {
										        // self signed cert
										        System.out.println("Self signed certificate.:"+x509cert.getSubjectDN());
										        try {
										            x509cert.verify(x509cert.getPublicKey());
										        } catch (Exception e) {
										            System.out.println("Self signed certificate verification failed.:"+x509cert.getSubjectDN());
										            throw e;
										        }
	                                            // self signed certificate is OK.
	                                            trustedCertificate = true;
										    }
										    else {
										        // NOT self signed
    	                                        Enumeration en = ks.aliases();
    	                                        String alias = "";
    	                                        java.security.cert.X509Certificate signCert = null;

    	                                        while (en.hasMoreElements()) {
    	                                            java.security.cert.X509Certificate storeCert = null;
    	                                            alias = (String) en.nextElement();

    	                                            if (ks.isCertificateEntry(alias)) {
    	                                                storeCert = (java.security.cert.X509Certificate) ks.getCertificate(alias);
    	                                                if (storeCert.getIssuerDN().getName().equals(issuerDN)) {
    	                                                    try {
    	                                                        x509cert.verify(storeCert.getPublicKey());
    	                                                        signCert = storeCert;
    	                                                        break;
    	                                                    } catch (Exception e) {
    	                                                        System.out.println("X509 keystore certificate verification failed.:"+storeCert.getSubjectDN());
    	                                                    }

    	                                                }
    	                                            }
    	                                        }
    	                                        if (signCert != null) {
    	                                            trustedCertificate = true;
    	                                        }
										    }
										}

										/*
										if(alias != null) {
											if (cert instanceof java.security.cert.X509Certificate) {
												try {
													( (java.security.cert.X509Certificate) cert).checkValidity();
													trustedCertificate = true;
												} catch(CertificateExpiredException cee) {
													// Accept all the certs!
													System.out.println("CertificateExpiredException detected.:"+cee);
												}
											}
										} else if (cert instanceof java.security.cert.X509Certificate) {
										    java.security.cert.X509Certificate x509cert = (java.security.cert.X509Certificate) cert;
										    PublicKey pubKey = x509cert.getPublicKey();

										    JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(x509cert);
										    X500Name x500name = certHolder.getSubject();
										    ContentVerifierProvider cvp = new BcRSAContentVerifierProviderBuilder(new DefaultDigestAlgorithmIdentifierFinder()).build(certHolder);
										    if (certHolder.isSignatureValid(cvp)) {
										        // Self Signed Certificate
										        trustedCertificate = true;
										        System.out.println("Self Signed Certificate:"+x500name);
										    }
										    else {
										        System.out.println("Certificate:"+x500name);
										    }



											// Accept SHA256 certs!
										    /*
											byte[] toSign = c.getTBSCertificate().getEncoded(ASN1Encoding.DER);
											System.out.println("SelfSigned Cert detected.:"+c.getIssuer().toString());

											ASN1Primitive asn1 = c.getSubjectPublicKeyInfo().parsePublicKey();
											if (asn1 instanceof ASN1Sequence) {
												ASN1Sequence seq = (ASN1Sequence) asn1;
												ASN1Primitive modulusPrim = seq.getObjectAt(0).toASN1Primitive();
												ASN1Primitive expPrim = seq.getObjectAt(1).toASN1Primitive();
												if (modulusPrim instanceof ASN1Integer &&
														expPrim instanceof ASN1Integer) {
													BigInteger modulus = ((ASN1Integer) modulusPrim).getPositiveValue();
													BigInteger exp = ((ASN1Integer) expPrim).getPositiveValue();
													AsymmetricKeyParameter mkey = new RSAKeyParameters(false, modulus, exp);

													byte[] actual = c.getSignature().getBytes();
													SHA256Digest digest = new SHA256Digest();

													RSADigestSigner signer = new RSADigestSigner(digest);
													signer.init(false, mkey);

													signer.update(toSign, 0, toSign.length);
													trustedCertificate = signer.verifySignature(actual);
												}
											}
										}
										*/

									}
									if (!trustedCertificate) {
										// error
										System.out.println("Not trusted certificate detected.");
										throw new CertificateException("Not trusted certificate detected.:"+host);
									}
									peertCerts = certs.toArray(new java.security.cert.Certificate[0]);
								} catch (Exception ex) {
									ex.printStackTrace();
									throw new IOException(ex);
								}
							}

							@Override
							public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException {
								return null;
							}

							private KeyStore loadKeyStore() throws Exception {
								FileInputStream trustStoreFis = null;
								try {
								    // Load the JDK's cacerts keystore file
								    String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
								    trustStoreFis = new FileInputStream(filename);

									KeyStore localKeyStore = null;

									String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType")!=null?System.getProperty("javax.net.ssl.trustStoreType"):KeyStore.getDefaultType();
									String trustStoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider")!=null?System.getProperty("javax.net.ssl.trustStoreProvider"):"";

									if (trustStoreType.length() != 0) {
										if (trustStoreProvider.length() == 0) {
											localKeyStore = KeyStore.getInstance(trustStoreType);
										} else {
											localKeyStore = KeyStore.getInstance(trustStoreType, trustStoreProvider);
										}

										char[] keyStorePass = null;
										String str5 = System.getProperty("javax.net.ssl.trustStorePassword")!=null?System.getProperty("javax.net.ssl.trustStorePassword"):"";
										if (str5.length() <= 0) {
										    str5 = "changeit";
										}
										if (str5.length() != 0) {
											keyStorePass = str5.toCharArray();
										}

										localKeyStore.load(trustStoreFis, keyStorePass);
										/*
								        Enumeration enumeration = localKeyStore.aliases();
								        while(enumeration.hasMoreElements()) {
								            String alias = (String)enumeration.nextElement();
								            System.out.println("alias name: " + alias);
								            java.security.cert.Certificate certificate = localKeyStore.getCertificate(alias);
								            System.out.println(certificate.toString());
							            }
                                        */
										if (keyStorePass != null) {
											for (int i = 0; i < keyStorePass.length; i++) {
												keyStorePass[i] = 0;
											}
										}
									}
									return localKeyStore;
								} finally {
									if (trustStoreFis != null) {
										trustStoreFis.close();
									}
								}
							}
						};
					}

				});
			} // startHandshake
		};
	}
}