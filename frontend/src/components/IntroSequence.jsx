import React, { useState } from 'react';
import './IntroSequence.css';
import '../styles/ancient-documents.css';
import audioService from '../utils/AudioService';

const IntroSequence = ({ onComplete }) => {
  const [currentDocument, setCurrentDocument] = useState(0);
  const [selectedChoice, setSelectedChoice] = useState(null);

  // Dados dos 4 documentos da sequência
  const documents = [
    {
      id: 'debt_notice',
      title: 'NOTIFICAÇÃO DE TRANSIÇÃO',
      type: 'Guilda Mercante de Alvor - Departamento de Recursos Trabalhistas',
      content: {
        header: 'PROGRAMA DE TRABALHO-CAPITANIA - ANO 1847',
        body: [
          'Prezado Ex-Trabalhador das Plantações,',
          '',
          'Seus anos de serviço leal nas colônias de Alvor foram reconhecidos.',
          'No entanto, sua dívida acumulada no sistema trabalhista totalizou 2.847 moedas de ouro.',
          '',
          'ORIGEM DA DÍVIDA:',
          '• Alojamento compulsório (3 anos): 1.200 moedas',
          '• Alimentação básica fornecida: 890 moedas',
          '• Ferramentas de trabalho obrigatórias: 340 moedas', 
          '• Juros compostos e taxas administrativas: 417 moedas',
          '',
          'OPORTUNIDADE DE QUITAÇÃO:',
          '',
          'A Guilda oferece uma alternativa aos trabalhos forçados nas minas:',
          'Torne-se capitão do navio "O Andarilho" e quite sua dívida através do comércio.',
          '',
          'Esta é sua única chance de liberdade.',
          'Escolha sabiamente.',
        ],
        footer: 'Diretor Aldrich Goldmane\n"Eficiência Através da Oportunidade"'
      }
    },
    {
      id: 'opportunity_contract',
      title: 'CONTRATO DE QUITAÇÃO',
      type: 'Guilda Mercante de Alvor - Departamento de Recuperação de Ativos',
      content: {
        header: 'TERMOS DE TRANSFERÊNCIA CONTROLADA - ANO 1847',
        body: [
          'Sua transição do trabalho nas plantações para capitão está autorizada.',
          '',
          'ATIVOS SOB SUPERVISÃO:',
          '• Navio "O Andarilho" (propriedade da Guilda até quitação)',
          '• Licença temporária de operação (renovável mensalmente)',
          '• Acesso restrito aos portos aprovados pela Guilda',
          '• Contratos pré-selecionados conforme disponibilidade',
          '',
          'OBRIGAÇÕES MENSAIS PERMANENTES:',
          '• Taxa de licença: 500 moedas (não negociável)',
          '• Seguro compulsório: 100 moedas (cobertura mínima)',
          '• Taxas portuárias: 50 moedas por atracação',
          '',
          'CONDIÇÕES DE LIBERDADE:',
          'Após quitação total da dívida inicial, capitães podem',
          'solicitar renegociação de termos (sujeito a aprovação).',
          '',
          'Lembre-se: A Guilda oferece estabilidade em troca de lealdade.',
          '',
          '(Cláusula 847: Inadimplência resulta em retorno imediato ao trabalho forçado)'
        ],
        footer: 'Diretor Aldrich Goldmane\n"Ordem e Prosperidade"'
      }
    },
    {
      id: 'ship_inventory', 
      title: 'CERTIFICADO DO NAVIO',
      type: 'O Andarilho - Relatório de Transferência',
      content: {
        header: 'AVALIAÇÃO TÉCNICA - NAVIO MERCANTE CLASSE B',
        body: [
          'ESTRUTURA GERAL: Funcional (necessita manutenção regular)',
          'CASCO: Sólido com desgaste normal de uso (85% integridade)',
          'VELAS: Operacionais (com alguns remendos, mas eficientes)',
          'CORDAME: Adequado (recomenda-se inspeção mensal)',
          'ÂNCORA: Funcional (com sinais de uso, mas confiável)',
          '',
          'SUPRIMENTOS DE TRANSFERÊNCIA:',
          '• Água potável: 5 barris (1 mês de navegação)',
          '• Provisões básicas: Biscoitos e carne seca (3 semanas)',
          '• Pólvora: 4 barris (quantidade padrão para mercante)',
          '• Kit médico: Suprimentos básicos de primeiros socorros',
          '',
          'TRIPULAÇÃO:',
          '• Status atual: Aguardando contratação pelo novo capitão',
          '• Recomendação: Mínimo 3 tripulantes para operação segura',
          '• Portos disponíveis: Tavernas em todos os portos de Alvor',
          '',
          'OBSERVAÇÕES DO INSPETOR:',
          '"Navio mercante padrão em condições adequadas para navegação.',
          'Perfeito para capitães iniciantes que buscam experiência.',
          'Com manutenção adequada, pode servir por muitos anos."',
          '',
          '- Inspetor Marcus Seaworth'
        ],
        footer: 'CERTIFICADO PARA NAVEGAÇÃO\n(Classe B - Mercante Padrão)'
      }
    },
    {
      id: 'first_choice',
      title: 'DEFININDO SEU DESTINO',
      type: 'Taverna do Porto - Alvor, Era da Vela (1847)',
      content: {
        header: 'QUAL SERÁ SEU PRIMEIRO CONTRATO?',
        body: [
          'Como capitão independente em Alvor,',
          'você deve escolher sua primeira oportunidade de trabalho.',
          'Esta decisão moldará sua reputação',
          'e influenciará suas opções futuras.',
          '',
          'Três propostas aguardam sua resposta:'
        ],
        choices: [
          {
            id: 'cooperate',
            title: 'COMÉRCIO ESTABELECIDO',
            description: 'Transporte de produtos especiais para propriedades agrícolas',
            details: [
              '• Pagamento garantido: 200 moedas',
              '• Risco: Baixo (sob proteção da Guilda)',
              '• Destino: Plantações de Nova Esperança',
              '• Desenvolve: Habilidades de Comércio e Navegação',
              '',
              '"Carregamento discreto de produtos especializados',
              'para operações agrícolas em grande escala."',
              '',
              '🎯 PROGRESSÃO: Expertise em rotas comerciais',
              '⚓ Estabilidade financeira e proteção institucional'
            ],
            impact: 'Integração ao sistema estabelecido - Crescimento previsível'
          },
          {
            id: 'resist', 
            title: 'VIDA LIVRE',
            description: 'Interceptação de navio mercante de luxo',
            details: [
              '• Recompensa estimada: 800 moedas',
              '• Risco: Muito Elevado (será perseguido)',
              '• Alvo: "Esperança Dourada" (carga de alto valor)',
              '• Desenvolve: Habilidades de Combate e Artilharia',
              '',
              '"Transporte de bens de luxo e mercadorias raras',
              'destinados aos setores mais abastados."',
              '',
              '⚔️ PROGRESSÃO: Autonomia através da força',
              '🏴‍☠️ Independência total, mas sem aliados institucionais'
            ],
            impact: 'Caminho independente - Liberdade com consequências'
          },
          {
            id: 'neutral',
            title: 'ROTAS ALTERNATIVAS', 
            description: 'Transporte de suprimentos médicos por vias não oficiais',
            details: [
              '• Recompensa: 50 moedas + rede de contatos',
              '• Risco: Extremo (operação não autorizada)',
              '• Destino: Porto Livre (comunidade independente)',
              '• Desenvolve: Habilidades de Exploração e Sobrevivência',
              '',
              '"Suprimentos médicos básicos com distribuição',
              'através de canais alternativos de comercialização."',
              '',
              '🗺️ PROGRESSÃO: Desenvolvimento de redes informais',
              '🤝 Crescimento através de parcerias comunitárias'
            ],
            impact: 'Abordagem colaborativa - Mudança através de comunidade'
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

  // Determinar classe do documento baseada no tipo
  const getDocumentClass = (docId) => {
    switch (docId) {
      case 'opportunity_contract':
        return 'ancient-document'; // Documentos oficiais da Guilda
      case 'ship_inventory':
        return 'nautical-chart'; // Certificado técnico náutico
      case 'debt_notice':
      case 'first_choice':
        return 'ancient-document'; // Documento de taverna
      default:
        return 'ancient-document';
    }
  };

  return (
    <div className="intro-sequence-container">
      <div className={`intro-document ${getDocumentClass(currentDoc.id)}`}>
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
            <button className="next-button ancient-button" onClick={handleNext}>
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
                <button className="confirm-button ancient-button" onClick={handleConfirm}>
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