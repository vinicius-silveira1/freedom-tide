import React, { useState } from 'react';
import './IntroSequence.css';
import audioService from '../utils/AudioService';

const IntroSequence = ({ onComplete }) => {
  const [currentDocument, setCurrentDocument] = useState(0);
  const [selectedChoice, setSelectedChoice] = useState(null);

  // Dados dos 4 documentos da sequência
  const documents = [
    {
      id: 'debt_notice',
      title: 'NOTIFICAÇÃO DE COBRANÇA',
      type: 'Guilda Mercante Unida - Departamento de Cobrança',
      content: {
        header: 'AVISO FINAL DE COBRANÇA',
        body: [
          'Cidadão,',
          '',
          'Suas dívidas trabalhistas acumuladas atingiram o montante de 2.847 moedas de ouro.',
          '',
          'DISCRIMINAÇÃO:',
          '• Alojamento no complexo trabalhista: 1.200 moedas',
          '• Alimentação básica (3 anos): 890 moedas',
          '• Ferramentas de trabalho: 340 moedas', 
          '• Juros e taxas administrativas: 417 moedas',
          '',
          'O pagamento integral é EXIGIDO em 72 horas.',
          '',
          'Caso não seja quitada, você será transferido para os trabalhos forçados nas minas de carvão conforme Artigo 847 do Código Mercantil.',
          '',
          'A Guilda é generosa, mas não tolera inadimplência.',
        ],
        footer: 'Coletor Jeremiah Brass\nDepartamento de Cobrança'
      }
    },
    {
      id: 'opportunity_contract',
      title: 'CONTRATO DE OPORTUNIDADE',
      type: 'Guilda Mercante Unida - Departamento de Recursos Humanos',
      content: {
        header: 'PROPOSTA EXCEPCIONAL DE QUITAÇÃO',
        body: [
          'A Guilda, em sua infinita misericórdia, oferece uma oportunidade única:',
          '',
          'QUITAÇÃO TOTAL de suas dívidas em troca de serviços marítimos.',
          '',
          'CONDIÇÕES:',
          '• Transferência do navio "O Andarilho" (sem tripulação)',
          '• Quitação imediata de todas as dívidas',
          '• Liberdade para operar como capitão independente',
          '',
          'OBRIGAÇÕES:',
          '• Taxa de transferência: 8.500 moedas',
          '• Licença de operação: 500 moedas/mês',
          '• Seguro obrigatório: 1.200 moedas/ano',
          '• Taxa de ancoragem em portos: 50 moedas/visita',
          '',
          'Esta é uma oportunidade que poucos recebem.',
          'Você tem 1 hora para assinar.',
          '',
          '(Cláusula 23: Em caso de inadimplência, o navio e todos os bens do capitão tornam-se propriedade da Guilda)'
        ],
        footer: 'Diretor Aldrich Goldmane\n"Sua Liberdade, Nosso Negócio"'
      }
    },
    {
      id: 'ship_inventory', 
      title: 'INVENTÁRIO DO NAVIO',
      type: 'O Andarilho - Inspeção Técnica',
      content: {
        header: 'RELATÓRIO DE ESTADO - NAVIO MERCANTE',
        body: [
          'CASCO: Deteriorado (múltiplas rachaduras, infiltrações)',
          'VELAS: Remendadas (eficiência reduzida em 40%)',
          'CORDAME: Desgastado (risco de rompimento)',
          'ÂNCORA: Enferrujada (funcionamento duvidoso)',
          '',
          'SUPRIMENTOS INCLUSOS:',
          '• Água potável: 3 barris (2 semanas)',
          '• Provisões: Biscoitos duros e carne seca (10 dias)',
          '• Pólvora: 2 barris (quantidade mínima)',
          '• Remédios: Kit básico (sem remédios para escorbuto)',
          '',
          'TRIPULAÇÃO:',
          '• NENHUMA - O capitão anterior abandonou o navio',
          '• Contratação necessária no porto de destino',
          '• Recomenda-se mínimo de 3 tripulantes para navegação',
          '',
          'OBSERVAÇÕES DO INSPETOR:',
          '"Este navio mal consegue navegar em águas calmas.',
          'Sem tripulação experiente, recomendo extrema cautela.',
          'Dos últimos 12 capitães similares, apenas 3 sobreviveram."',
          '',
          '- Inspetor Marcus Seaworth'
        ],
        footer: 'APROVADO PARA NAVEGAÇÃO\n(Com ressalvas)'
      }
    },
    {
      id: 'first_choice',
      title: 'SUA PRIMEIRA DECISÃO',
      type: 'Mesa do Porto - Contratos Disponíveis',
      content: {
        header: 'TRÊS CAMINHOS SE ABREM DIANTE DE VOCÊ',
        body: [
          'Com um navio questionável e uma dívida impossível,',
          'você deve escolher seu primeiro contrato.',
          '',
          'Cada escolha definirá quem você se tornará:'
        ],
        choices: [
          {
            id: 'cooperate',
            title: 'CONTRATO DA GUILDA',
            description: 'Transportar "especiarias" para as colônias',
            details: [
              '• Pagamento: 200 moedas',
              '• Risco: Baixo',
              '• Destino: Plantações de Nova Esperança',
              '',
              '"Produto especial para manter a produtividade',
              'dos trabalhadores em níveis ideais..."'
            ],
            impact: 'Trabalho estável, bem remunerado'
          },
          {
            id: 'resist', 
            title: 'SAQUEAR MERCADOR',
            description: 'Atacar navio mercante desprotegido',
            details: [
              '• Pagamento: 800 moedas (estimado)',
              '• Risco: Alto',
              '• Alvo: Navio "Esperança Dourada"',
              '',
              '"Carregamento inclui provisões familiares',
              'e pertences pessoais de colonos..."'
            ],
            impact: 'Dinheiro rápido, sem patrões'
          },
          {
            id: 'neutral',
            title: 'CONTRABANDO MÉDICO', 
            description: 'Levar medicamentos para colônia isolada',
            details: [
              '• Pagamento: 50 moedas',
              '• Risco: Muito Alto',
              '• Destino: Colônia de Porto Livre',
              '',
              '"Remédios foram declarados contrabando',
              'por questões de "regulamentação comercial"..."'
            ],
            impact: 'Pagamento baixo, mas satisfação pessoal'
          }
        ]
      }
    }
  ];

  const handleNext = () => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    
    if (currentDocument < documents.length - 1) {
      setCurrentDocument(currentDocument + 1);
    }
  };

  const handleChoice = (choiceId) => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    setSelectedChoice(choiceId);
  };

  const handleConfirm = () => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    
    // Passa a escolha para o componente pai (App.jsx)
    onComplete(selectedChoice);
  };

  const currentDoc = documents[currentDocument];
  const isLastDocument = currentDocument === documents.length - 1;

  return (
    <div className="intro-sequence-container">
      <div className="intro-document">
        {/* Cabeçalho do documento */}
        <div className="document-header">
          <h1 className="document-title">{currentDoc.title}</h1>
          <p className="document-type">{currentDoc.type}</p>
        </div>

        {/* Conteúdo do documento */}
        <div className="document-content">
          <h2 className="content-header">{currentDoc.content.header}</h2>
          
          <div className="content-body">
            {currentDoc.content.body.map((line, index) => (
              <p key={index} className={line.startsWith('•') ? 'bullet-point' : 'text-line'}>
                {line}
              </p>
            ))}
          </div>

          {/* Escolhas (apenas no último documento) */}
          {isLastDocument && currentDoc.content.choices && (
            <div className="choices-section">
              {currentDoc.content.choices.map((choice) => (
                <div 
                  key={choice.id}
                  className={`choice-option ${selectedChoice === choice.id ? 'selected' : ''}`}
                  onClick={() => handleChoice(choice.id)}
                >
                  <h3 className="choice-title">{choice.title}</h3>
                  <p className="choice-description">{choice.description}</p>
                  
                  <div className="choice-details">
                    {choice.details.map((detail, index) => (
                      <p key={index} className="detail-line">{detail}</p>
                    ))}
                  </div>
                  
                  <div className="choice-impact">
                    <strong>Impacto:</strong>
                    <br />
                    {choice.impact}
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Rodapé do documento */}
          {currentDoc.content.footer && (
            <div className="document-footer">
              {currentDoc.content.footer.split('\n').map((line, index) => (
                <p key={index}>{line}</p>
              ))}
            </div>
          )}
        </div>

        {/* Botões de navegação */}
        <div className="document-controls">
          {!isLastDocument && (
            <button className="next-button" onClick={handleNext}>
              Continuar Lendo
            </button>
          )}
          
          {isLastDocument && (
            <>
              {!selectedChoice && (
                <p className="instruction-text">
                  Escolha seu caminho clicando em uma das opções acima.
                </p>
              )}
              
              {selectedChoice && (
                <button className="confirm-button" onClick={handleConfirm}>
                  Aceitar Destino
                </button>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default IntroSequence;