/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
/*
 * SubStorage.java
 *
 * Created on October 31, 2004, 12:45 PM
 */

package org.netbeans.lib.storage;
import org.netbeans.api.storage.Storage;
import java.nio.*;
import java.io.*;

/**
 * A read-only Storage over a single ByteBuffer, used to create sub-storages
 * of a master storage.
 *
 * @author Tim Boudreau
 */
public class SubStorage extends Storage {
    private final ByteBuffer master;
    private final int size;

    /** Creates a new instance of ByteBufferStorag */
    public SubStorage(ByteBuffer buf) {
        this.master = buf;
       size = buf.limit();
    }

    public java.nio.ByteBuffer getReadBuffer(long start, long length) throws java.io.IOException {
        master.position((int) start);
        master.limit((int) length);
        return master.slice();
    }

    public boolean isClosed() {
        return false;
    }

    public long lastWrite() {
        return 0;
    }

    public int size() {
        return size;
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public void truncate(long end) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public int write(java.nio.ByteBuffer buf) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public java.nio.ByteBuffer getWriteBuffer(int length) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public long replace(java.nio.ByteBuffer buf, long start, long end) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Throws an UnsupportedOperationException.
     */
    public void excise(long start, long end) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public void flush() throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Does nothing
     */
    public void close() throws java.io.IOException {
        //do nothing
    }

    /**
     * Does nothing
     */
    public void dispose() {
        //do nothing
    }
}
