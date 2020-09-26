/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/* Generated By:JavaCC: Do not edit this line. DefaultCharStream.java Version 5.0 */
/* JavaCCOptions:STATIC=false,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package tech.units.indriya.internal.format;

import javax.measure.MeasurementError;

/**
 * An implementation of interface CharStream, where the stream is assumed to contain only ASCII characters (without Unicode processing).
 *
 * @version 5.3, September 27, 2020
 */

final class DefaultCharStream {
  /** Whether parser is static. */
  public static final boolean staticFlag = false;
  private int bufsize;
  private int available;
  private int tokenBegin;
  /** Position in buffer. */
  public int bufpos = -1;
  protected int bufline[];
  protected int bufcolumn[];

  protected int column = 0;
  protected int line = 1;

  protected boolean prevCharIsCR = false;
  protected boolean prevCharIsLF = false;

  protected java.io.Reader inputStream;

  protected char[] buffer;
  protected int maxNextCharInd = 0;
  protected int inBuf = 0;
  protected int tabSize = 8;

  protected void setTabSize(int i) {
    tabSize = i;
  }

  protected int getTabSize() {
    return tabSize;
  }

  protected void expandBuff(boolean wrapAround) {
    char[] newbuffer = new char[bufsize + 2048];
    int newbufline[] = new int[bufsize + 2048];
    int newbufcolumn[] = new int[bufsize + 2048];

    try {
      if (wrapAround) {
        System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
        System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
        buffer = newbuffer;

        System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
        System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);
        bufline = newbufline;

        System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
        System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
        bufcolumn = newbufcolumn;

        maxNextCharInd = (bufpos += (bufsize - tokenBegin));
      } else {
        System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
        buffer = newbuffer;

        System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
        bufline = newbufline;

        System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
        bufcolumn = newbufcolumn;

        maxNextCharInd = (bufpos -= tokenBegin);
      }
    } catch (Throwable t) {
      throw new MeasurementError(t.getMessage());
    }

    bufsize += 2048;
    available = bufsize;
    tokenBegin = 0;
  }

  protected void fillBuff() throws java.io.IOException {
    if (maxNextCharInd == available) {
      if (available == bufsize) {
        if (tokenBegin > 2048) {
          bufpos = maxNextCharInd = 0;
          available = tokenBegin;
        } else if (tokenBegin < 0)
          bufpos = maxNextCharInd = 0;
        else
          expandBuff(false);
      } else if (available > tokenBegin)
        available = bufsize;
      else if ((tokenBegin - available) < 2048)
        expandBuff(true);
      else
        available = tokenBegin;
    }

    int i;
    try {
      if ((i = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1) {
        inputStream.close();
        throw new java.io.IOException();
      }
      maxNextCharInd += i;
    } catch (java.io.IOException e) {
      --bufpos;
      backup(0);
      if (tokenBegin == -1)
        tokenBegin = bufpos;
      throw e;
    }
  }

  /** Start. */
  public char beginToken() throws java.io.IOException {
    tokenBegin = -1;
    char c = readChar();
    tokenBegin = bufpos;

    return c;
  }

  protected void updateLineColumn(char c) {
    column++;

    if (prevCharIsLF) {
      prevCharIsLF = false;
      line += (column = 1);
    } else if (prevCharIsCR) {
      prevCharIsCR = false;
      if (c == '\n') {
        prevCharIsLF = true;
      } else
        line += (column = 1);
    }

    switch (c) {
      case '\r':
        prevCharIsCR = true;
        break;
      case '\n':
        prevCharIsLF = true;
        break;
      case '\t':
        column--;
        column += (tabSize - (column % tabSize));
        break;
      default:
        break;
    }

    bufline[bufpos] = line;
    bufcolumn[bufpos] = column;
  }

  /** Read a character. */
  public char readChar() throws java.io.IOException {
    if (inBuf > 0) {
      --inBuf;

      if (++bufpos == bufsize)
        bufpos = 0;

      return buffer[bufpos];
    }

    if (++bufpos >= maxNextCharInd)
      fillBuff();

    char c = buffer[bufpos];

    updateLineColumn(c);
    return c;
  }

  @Deprecated
  /**
   * @deprecated
   * @see #getEndColumn
   */
  public int getColumn() {
    return bufcolumn[bufpos];
  }

  /** Get token end column number. */
  public int getEndColumn() {
    return bufcolumn[bufpos];
  }

  /** Get token end line number. */
  public int getEndLine() {
    return bufline[bufpos];
  }

  /** Get token beginning column number. */
  public int getBeginColumn() {
    return bufcolumn[tokenBegin];
  }

  /** Get token beginning line number. */
  public int getBeginLine() {
    return bufline[tokenBegin];
  }

  /** Backup a number of characters. */
  public void backup(int amount) {

    inBuf += amount;
    if ((bufpos -= amount) < 0)
      bufpos += bufsize;
  }

  /** Constructor. */
  public DefaultCharStream(java.io.Reader dstream, int startline, int startcolumn, int buffersize) {
    inputStream = dstream;
    line = startline;
    column = startcolumn - 1;

    available = bufsize = buffersize;
    buffer = new char[buffersize];
    bufline = new int[buffersize];
    bufcolumn = new int[buffersize];
  }

  /** Constructor. */
  public DefaultCharStream(java.io.Reader dstream, int startline, int startcolumn) {
    this(dstream, startline, startcolumn, 4096);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.Reader dstream) {
    this(dstream, 1, 1, 4096);
  }

  /** Reinitialise. */
  public void reInit(java.io.Reader dstream, int startline, int startcolumn, int buffersize) {
    inputStream = dstream;
    line = startline;
    column = startcolumn - 1;

    if (buffer == null || buffersize != buffer.length) {
      available = bufsize = buffersize;
      buffer = new char[buffersize];
      bufline = new int[buffersize];
      bufcolumn = new int[buffersize];
    }
    prevCharIsLF = prevCharIsCR = false;
    tokenBegin = inBuf = maxNextCharInd = 0;
    bufpos = -1;
  }

  /** Reinitialise. */
  public void reInit(java.io.Reader dstream, int startline, int startcolumn) {
    reInit(dstream, startline, startcolumn, 4096);
  }

  /** Reinitialise. */
  public void reInit(java.io.Reader dstream) {
    reInit(dstream, 1, 1, 4096);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
      throws java.io.UnsupportedEncodingException {
    this(encoding == null ? new java.io.InputStreamReader(dstream) : new java.io.InputStreamReader(dstream, encoding), startline, startcolumn,
        buffersize);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.InputStream dstream, int startline, int startcolumn, int buffersize) {
    this(new java.io.InputStreamReader(dstream), startline, startcolumn, buffersize);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.InputStream dstream, String encoding, int startline, int startcolumn) throws java.io.UnsupportedEncodingException {
    this(dstream, encoding, startline, startcolumn, 4096);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.InputStream dstream, int startline, int startcolumn) {
    this(dstream, startline, startcolumn, 4096);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.InputStream dstream, String encoding) throws java.io.UnsupportedEncodingException {
    this(dstream, encoding, 1, 1, 4096);
  }

  /** Constructor. */
  public DefaultCharStream(java.io.InputStream dstream) {
    this(dstream, 1, 1, 4096);
  }

  /** Reinitialise. */
  public void reInit(java.io.InputStream dstream, String encoding, int startline, int startcolumn, int buffersize)
      throws java.io.UnsupportedEncodingException {
    reInit(encoding == null ? new java.io.InputStreamReader(dstream) : new java.io.InputStreamReader(dstream, encoding), startline, startcolumn,
        buffersize);
  }

  /** Reinitialise. */
  public void reInit(java.io.InputStream dstream, int startline, int startcolumn, int buffersize) {
    reInit(new java.io.InputStreamReader(dstream), startline, startcolumn, buffersize);
  }

  /** Reinitialise. */
  public void reInit(java.io.InputStream dstream, String encoding) throws java.io.UnsupportedEncodingException {
    reInit(dstream, encoding, 1, 1, 4096);
  }

  /** Reinitialise. */
  public void reInit(java.io.InputStream dstream) {
    reInit(dstream, 1, 1, 4096);
  }

  /** Reinitialise. */
  public void reInit(java.io.InputStream dstream, String encoding, int startline, int startcolumn) throws java.io.UnsupportedEncodingException {
    reInit(dstream, encoding, startline, startcolumn, 4096);
  }

  /** Reinitialise. */
  public void reInit(java.io.InputStream dstream, int startline, int startcolumn) {
    reInit(dstream, startline, startcolumn, 4096);
  }

  /** Get token literal value. */
  public String getImage() {
    if (bufpos >= tokenBegin) return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
    return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
  }

  /** Get the suffix. */
  public char[] GetSuffix(int len) {
    char[] ret = new char[len];

    if ((bufpos + 1) >= len)
      System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
    else {
      System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0, len - bufpos - 1);
      System.arraycopy(buffer, 0, ret, len - bufpos - 1, bufpos + 1);
    }

    return ret;
  }

  /** Reset buffer when finished. */
  public void done() {
    buffer = null;
    bufline = null;
    bufcolumn = null;
  }

  /**
   * Method to adjust line and column numbers for the start of a token.
   */
  public void adjustBeginLineColumn(int newLine, int newCol) {
    int start = tokenBegin;
    int len;
    int i = 0;
    int j = 0;
    int k = 0;
    int nextColDiff = 0;
    int columnDiff = 0;

    if (bufpos >= tokenBegin) {
      len = bufpos - tokenBegin + inBuf + 1;
    } else {
      len = bufsize - tokenBegin + bufpos + 1 + inBuf;
    }
    
    while (i < len && bufline[j = start % bufsize] == bufline[k = ++start % bufsize]) {
      bufline[j] = newLine;
      nextColDiff = columnDiff + bufcolumn[k] - bufcolumn[j];
      bufcolumn[j] = newCol + columnDiff;
      columnDiff = nextColDiff;
      i++;
    }

    if (i < len) {
      bufline[j] = newLine++;
      bufcolumn[j] = newCol + columnDiff;

      while (i++ < len) {
        if (bufline[j = start % bufsize] != bufline[++start % bufsize])
          bufline[j] = newLine++;
        else
          bufline[j] = newLine;
      }
    }

    line = bufline[j];
    column = bufcolumn[j];
  }

}
/*
 * JavaCC - OriginalChecksum=ec4e178f3ccf05ea2ca32d15e09312ca (do not edit this
 * line)
 */
