/*
 *  Copyright 2008 Anuradha.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.netbeans.cubeon.bugzilla.api.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream implementation that will provide stream progress monitoring.
 *
 * @author radoslaw.holewa
 */
public class ProgressMonitorInputStream extends BufferedInputStream {

    /**
     * Progress monitor that will be used to inform about input stream processing state.
     */
    private ProgressMonitor progressMonitor;

    /**
     * Two-arguments constructor that will be used to create instance of this class, which will provide
     * stream monitoring.
     *
     * @param inputStream     - base input stream
     * @param progressMonitor - progress monitor that will be used to inform about input stream
     *                        processing state.
     */
    public ProgressMonitorInputStream( InputStream inputStream, ProgressMonitor progressMonitor ) {
      super( inputStream );
      if( progressMonitor == null ) {
        throw new IllegalStateException( "Progress monitor should not be null!" );
      }
      this.progressMonitor = progressMonitor;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized int read() throws IOException {
      int numOfBytes = super.read();
      progressMonitor.setBytesReaded( pos );
      return numOfBytes;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized int read( byte b[], int off, int len ) throws IOException {
      int numOfBytes = super.read( b, off, len );
      progressMonitor.setBytesReaded( pos );
      return numOfBytes;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long skip( long n ) throws IOException {
      long numOfBytes = super.skip( n );
      progressMonitor.setBytesReaded( pos );
      return numOfBytes;
    }

    /**
     * {@inheritDoc}
     */
    public int read( byte b[] ) throws IOException {
      int numOfBytes = super.read( b );
      progressMonitor.setBytesReaded( pos );
      return numOfBytes;
    }
}
