/*
 *  Copyright 2015 SmartBear Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at

 *     http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package ut.com.smartbear.collaborator.admin;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AdminServletTest {

    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;

    @Before
    public void setup() {
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSomething() {
        String expected = "test";
        when(mockRequest.getParameter(Mockito.anyString())).thenReturn(expected);
        assertEquals(expected,mockRequest.getParameter("some string"));

    }
}
