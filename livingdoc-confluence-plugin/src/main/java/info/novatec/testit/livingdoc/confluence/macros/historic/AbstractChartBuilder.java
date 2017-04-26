/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package info.novatec.testit.livingdoc.confluence.macros.historic;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;

import com.atlassian.confluence.servlet.download.ExportDownload;

import info.novatec.testit.livingdoc.server.LivingDocServerException;


public abstract class AbstractChartBuilder {

    protected static final String DEFAULT_FONT_NAME = "Helvetica";
    protected static final Font DEFAULT_TITLE_FONT = new Font(DEFAULT_FONT_NAME, Font.BOLD, 12);
    protected static final Font DEFAULT_SUBTITLE_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, 11);
    protected static final Font DEFAULT_SUBTITLE2_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, 11);
    protected static final Font DEFAULT_AXIS_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, 10);
    protected static final Font DEFAULT_LABEL_FONT = new Font(DEFAULT_FONT_NAME, Font.BOLD, 10);
    protected static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
    protected static final Color GREEN_COLOR = new Color(Integer.parseInt("33cc00", 16));

    private ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();

    protected HistoricParameters settings;

    protected AbstractChartBuilder(HistoricParameters settings) {
        this.settings = settings;
    }

    public abstract String generateChart() throws LivingDocServerException;

    public String getChartMap(String chartMapId) throws IOException {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        try {
            ChartUtilities.writeImageMap(pw, chartMapId, chartRenderingInfo, new StandardToolTipTagFragmentGenerator(),
                new StandardURLTagFragmentGenerator());
        } finally {
            IOUtils.closeQuietly(pw);
        }

        return writer.toString();
    }

    @SuppressWarnings("deprecation")
    protected String getDownloadPath(BufferedImage chartImage) throws IOException {
        File imageOutputFile = ExportDownload.createTempFile("chart", ".png");

        ImageIO.write(chartImage, "png", imageOutputFile);

        return ExportDownload.getUrl(imageOutputFile, "image/png");
    }

    protected BufferedImage createChartImage(JFreeChart chart) {
        return chart.createBufferedImage(settings.getWidth(), settings.getHeight(), chartRenderingInfo);
    }

    protected void customizeTitle(TextTitle title, Font font) {
        title.setFont(font);
        title.setTextAlignment(HorizontalAlignment.LEFT);
        title.setPaint(Color.BLACK);
        title.setBackgroundPaint(TRANSPARENT_COLOR);
    }

    protected void customizeAxis(Axis axis) {
        axis.setLabelFont(DEFAULT_LABEL_FONT);
        axis.setTickLabelFont(DEFAULT_AXIS_FONT);
    }

    protected void addSubTitle(JFreeChart chart, String subTitle, Font font) {
        if (StringUtils.isNotEmpty(subTitle)) {
            TextTitle chartSubTitle = new TextTitle(subTitle);
            customizeTitle(chartSubTitle, font);
            chart.addSubtitle(chartSubTitle);
        }
    }
}
