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
package org.netbeans.cubeon.ui.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

/**
 *
 * @author Anuradha
 */
public class PaintUtils {

    public static  void drawCurve(Graphics2D g2,
            float y1, float y1_offset,
            float y2, float y2_offset,
            float cx1, float cx1_offset,
            float cy1, float cy1_offset,
            float cx2, float cx2_offset,
            float cy2, float cy2_offset,
            float thickness,
            float speed,
            boolean invert, int counter, float width, Color start, Color end) {


        float offset = (float) Math.sin(counter / (speed * Math.PI));

        float start_x = 0.0f;
        float start_y = offset * y1_offset + y1;
        float end_x = width;
        float end_y = offset * y2_offset + y2;

        float ctrl1_x = offset * cx1_offset + cx1;
        float ctrl1_y = offset * cy1_offset + cy1;
        float ctrl2_x = offset * cx2_offset + cx2;
        float ctrl2_y = offset * cy2_offset + cy2;

        GeneralPath thickCurve = new GeneralPath();
        thickCurve.moveTo(start_x, start_y);
        thickCurve.curveTo(ctrl1_x, ctrl1_y,
                ctrl2_x, ctrl2_y,
                end_x, end_y);
        thickCurve.lineTo(end_x, end_y + thickness);
        thickCurve.curveTo(ctrl2_x, ctrl2_y + thickness,
                ctrl1_x, ctrl1_y + thickness,
                start_x, start_y + thickness);
        thickCurve.lineTo(start_x, start_y);

        Rectangle bounds = thickCurve.getBounds();
        if (!bounds.intersects(g2.getClipBounds())) {
            return;
        }

        GradientPaint painter = new GradientPaint(0, bounds.y,
                invert ? end : start,
                0, bounds.y + bounds.height,
                invert ? start : end);

        Paint oldPainter = g2.getPaint();
        g2.setPaint(painter);
        g2.fill(thickCurve);

        g2.setPaint(oldPainter);
    }
}
