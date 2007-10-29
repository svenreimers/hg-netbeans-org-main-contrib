/*
 * Copyright (c) 2007, Sun Microsystems, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems, Inc. nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package groups;

import javafx.ui.*;
import javafx.ui.canvas.*;

class GroupsExample extends CompositeNode {
    attribute transformation: Transform*;
}

function GroupsExample.composeNode() = Group {
        transform: bind transformation
        content:
        [Rect {
            x: 20
            y: 20
            height: 80
            width: 300
            arcHeight: 20
            arcWidth: 20
            fill: cyan
            stroke: purple
            strokeWidth: 2
        },
        Ellipse {
            cx: 150
            cy: 80
            radiusX: 100
            radiusY: 50
            fill: orange
            stroke: blue
            strokeWidth: 2
        },
        Polygon {
             points: [5, 5, 25, 5, 15, 25]
             fill: gray
             stroke: black
             strokeWidth: 3
        }]
    };

Canvas {
    var node = GroupsExample{
        transform: translate(150,50)
    }
    content:[node,
        View {
            content: Label{
                text:
"<html> 
   <body>
       <p>Click the links below to see the effect of several different shapes.</p>                
       <li><a href='{#(operation(){node.transformation = [];})}'>None</a></li>
       <li><a href='{#(operation(){node.transformation = translate(100, 20);})}'>Translate</a></li>
       <li><a href='{#(operation(){node.transformation = rotate(20, 0, 0);})}'>Rotate</a></li>
       <li><a href='{#(operation(){node.transformation = scale(2.0, 2.0);})}'>Scale</a></li>
       <li><a href='{#(operation(){node.transformation =skew(10, 10);})}'>Skew</a></li>
   </body>
</html>"                
            }
        }
    ]
}    