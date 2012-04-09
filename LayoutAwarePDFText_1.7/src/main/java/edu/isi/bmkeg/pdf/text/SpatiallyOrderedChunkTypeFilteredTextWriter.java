package edu.isi.bmkeg.pdf.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import edu.isi.bmkeg.pdf.model.ChunkBlock;
import edu.isi.bmkeg.pdf.model.Document;
import edu.isi.bmkeg.pdf.model.PageBlock;
import edu.isi.bmkeg.pdf.model.ordering.SpatialOrdering;

public class SpatiallyOrderedChunkTypeFilteredTextWriter implements TextWriter
{
	private boolean appendFigureLegendsToEnd;
	private boolean appendTablesToEnd;
	private StringBuilder text;
	private StringBuilder figureLegend;
	private StringBuilder tableText;
	public SpatiallyOrderedChunkTypeFilteredTextWriter(boolean appendFigureLegendsToEnd,boolean appendTablesToEnd)
	{
		this.appendFigureLegendsToEnd = appendFigureLegendsToEnd;
		this.appendTablesToEnd = appendFigureLegendsToEnd;
	}
	
	@Override
	public void write(Document document, String outputFilename) throws IOException,FileNotFoundException
	{
		text = new StringBuilder();
		if(appendFigureLegendsToEnd){
			figureLegend = new StringBuilder();
		}
		if(appendTablesToEnd){
			tableText = new StringBuilder();
		}
		int totalNumberOfPages = document.getTotalNumberOfPages();
		PageBlock page;
		boolean startRecording = true;
		for (int i = 1; i <= totalNumberOfPages; i++)
		{
			page = document.getPage(i);
			List<ChunkBlock> chunksPerPage = page.getAllChunkBlocks(SpatialOrdering.PAGE_COLUMN_AWARE_MIXED_MODE);
			for(ChunkBlock chunkBlock:chunksPerPage){
				if(chunkBlock.getType().equals(ChunkBlock.TYPE_ABSTRACT_HEADING)||chunkBlock.getType().equals(ChunkBlock.TYPE_ABSTRACT_BODY)||chunkBlock.getType().equals(ChunkBlock.TYPE_INTRODUCTION_HEADING)||chunkBlock.getType().equals(ChunkBlock.TYPE_INTRODUCTION_BODY)){
					startRecording = true;
				}
				if (startRecording)
				{
					if (!chunkBlock.getType().equals(ChunkBlock.TYPE_TABLE)&&!chunkBlock.getType().equals(ChunkBlock.TYPE_FIGURE_LEGEND) && !chunkBlock.getType().equals(chunkBlock.TYPE_FOOTER) && !chunkBlock.getType().equals(chunkBlock.TYPE_HEADER)&& !chunkBlock.getType().equals(chunkBlock.TYPE_AUTHORS)&& !chunkBlock.getType().equals(chunkBlock.TYPE_AFFLIATION))
					{
						text.append(chunkBlock.getchunkText() + "\n");
						
					}
					if (chunkBlock.getType().equals(ChunkBlock.TYPE_FIGURE_LEGEND))
					{
						if (appendFigureLegendsToEnd)
						{
							figureLegend.append(chunkBlock.getchunkText() + "\n");
						}else{
							text.append(chunkBlock.getchunkText() + "\n");
						}
					}
					if(chunkBlock.getType().equals(ChunkBlock.TYPE_TABLE)){
						if(appendTablesToEnd){
							tableText.append(chunkBlock.getchunkText() + "\n");
						}else{
							text.append(chunkBlock.getchunkText() + "\n");
						}
					}
				}
			}
		}
		if (appendFigureLegendsToEnd)
		{
			text.append(figureLegend);
		}
		if(appendTablesToEnd){
			text.append(tableText);
		}
		ReadWriteTextFileWithEncoding.write(outputFilename, TextWriter.UTF_8, text.toString());
		
	}

		
	

}
